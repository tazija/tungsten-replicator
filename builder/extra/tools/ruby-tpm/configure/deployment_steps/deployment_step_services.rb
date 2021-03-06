module ConfigureDeploymentStepServices
  def get_methods
    [
      ConfigureDeploymentMethod.new("apply_config_services", 0, ConfigureDeployment::FINAL_STEP_WEIGHT),
      ConfigureCommitmentMethod.new("set_maintenance_policy", ConfigureDeployment::FIRST_GROUP_ID, ConfigureDeployment::FIRST_STEP_WEIGHT),
      ConfigureCommitmentMethod.new("stop_replication_services", -1, 0),
      ConfigureCommitmentMethod.new("update_metadata", 1, 0),
      ConfigureCommitmentMethod.new("deploy_services", 1, 1),
      ConfigureCommitmentMethod.new("start_replication_services", 1, ConfigureDeployment::FINAL_STEP_WEIGHT),
      ConfigureCommitmentMethod.new("wait_for_manager", 2, -1),
      ConfigureCommitmentMethod.new("set_automatic_policy", 3, 0),
      ConfigureCommitmentMethod.new("start_connector", 4, 1, false),
      ConfigureCommitmentMethod.new("set_original_policy", 4, 2),
      ConfigureCommitmentMethod.new("report_services", ConfigureDeployment::FINAL_GROUP_ID, ConfigureDeployment::FINAL_STEP_WEIGHT-1, false),
      ConfigureCommitmentMethod.new("check_ping", ConfigureDeployment::FINAL_GROUP_ID, ConfigureDeployment::FINAL_STEP_WEIGHT)
    ]
  end
  module_function :get_methods
  
  # Set up files and perform other configuration for services.
  def apply_config_services
    Configurator.instance.write_header "Performing services configuration"

    config_wrapper()
    
    write_deployall()
    write_undeployall()
    write_startall()
    write_stopall()
    
    prepare_dir = get_deployment_basedir()
    out = File.open(prepare_dir + "/.watchfiles", "w")
    
    @watchfiles.uniq().each{
      |file|
      
      FileUtils.cp(file, get_original_watch_file(file))
      if file =~ /#{prepare_dir}/
        file_to_watch = file.sub(prepare_dir, "")
        if file_to_watch[0, 1] == "/"
          file_to_watch.slice!(0)
        end 
      else
        file_to_watch = file
      end
      out.puts file_to_watch
    }
    out.close
  end
  
  def deploy_services
    if get_additional_property(ACTIVE_DIRECTORY_PATH)
      return
    end
    
    unless @config.getProperty(SVC_INSTALL) == "true"
      return
    end
    
    info("Installing services")
    begin
      installed = cmd_result("#{@config.getProperty(CURRENT_RELEASE_DIRECTORY)}/cluster-home/bin/deployall")
      info(installed)
    rescue CommandError => e
      warning("Unable to install the Tungsten services for start on system boot.  This may occur if the services have already been installed.")
      warning("The message returned was: #{e.errors}")
    end
  end
  
  def start_connector
    # We have been told not to restart the connector
    if get_additional_property(RESTART_CONNECTORS) == false
      debug("Don't restart the connector service")
      return
    end
    
    unless is_connector?() == true
      return
    end
    
    if get_additional_property(ACTIVE_DIRECTORY_PATH) && get_additional_property(CONNECTOR_IS_RUNNING) == "true"
      info("Stopping the old connector")
      info(cmd_result("#{get_additional_property(ACTIVE_DIRECTORY_PATH)}/tungsten-connector/bin/connector stop"))
    end
    
    if get_additional_property(ACTIVE_DIRECTORY_PATH) && get_additional_property(CONNECTOR_ENABLED) == "true"
      if get_additional_property(CONNECTOR_IS_RUNNING) == "true"
        info("Starting the connector")
        info(cmd_result("#{@config.getProperty(CURRENT_RELEASE_DIRECTORY)}/tungsten-connector/bin/connector start"))
      end
    elsif @config.getProperty(SVC_START) == "true"
      info("Starting the connector")
      info(cmd_result("#{@config.getProperty(CURRENT_RELEASE_DIRECTORY)}/tungsten-connector/bin/connector start"))
    end
  end
  
  def report_services
    if @config.getProperty(SVC_REPORT) == "true"
      super(nil)
    end
  end
  
  def check_ping(level = Logger::NOTICE)
    if @config.getProperty(SVC_REPORT) == "true" && is_manager?() && manager_is_running?()
      cmd_result("echo 'ping' | #{get_cctrl_cmd()}").scan(/HOST ([a-zA-Z0-9\-\.]+)\/[0-9\.]+: NOT REACHABLE/).each{
        |match|
        warning("Unable to ping the host on #{match[0]}")
      }
    end
    
    if is_replicator?() && replicator_is_running?()
      begin
        error_lines = cmd_result("#{get_trepctl_cmd()} services | grep ERROR | wc -l")
        if error_lines.to_i() > 0
          error("At least one replication service has experienced an error")
        end
      rescue CommandError
        error("Unable to check if the replication services are working properly")
      end
    end
  end
  
  def config_wrapper
    # Patch for Ubuntu 64-bit start-up problem.
    if Configurator.instance.distro?() == OS_DISTRO_DEBIAN && Configurator.instance.arch?() == OS_ARCH_64
      wrapper_file = "#{get_deployment_basedir()}/cluster-home/bin/wrapper-linux-x86-32"
      if File.exist?(wrapper_file)
        FileUtils.rm("#{get_deployment_basedir()}/cluster-home/bin/wrapper-linux-x86-32")
      end
    end
  end
  
  def write_startall
    # Create startall script.
    script = "#{get_deployment_basedir()}/cluster-home/bin/startall"
    out = File.open(script, "w")
    out.puts "#!/bin/bash"
    out.puts "# Start all services using local service scripts"
    out.puts "THOME=`dirname $0`/../.."
    out.puts "cd $THOME"
    @services.each { |svc| out.puts svc + " start" }
    out.puts "# AUTO-CONFIGURED: #{DateTime.now}"
    out.chmod(0755)
    out.close
    info "GENERATED FILE: " + script
  end

  def write_stopall
    # Create stopall script.
    script = "#{get_deployment_basedir()}/cluster-home/bin/stopall"
    out = File.open(script, "w")
    out.puts "#!/bin/bash"
    out.puts "# Stop all services using local service scripts"
    out.puts "THOME=`dirname $0`/../.."
    out.puts "cd $THOME"
    @services.reverse_each { |svc| out.puts svc + " stop" }
    out.puts "# AUTO-CONFIGURED: #{DateTime.now}"
    out.chmod(0755)
    out.close
    info "GENERATED FILE: " + script
  end

  def write_deployall
    # Create deployall script.
    if Configurator.instance.can_install_services_on_os?()
      script = "#{get_deployment_basedir()}/cluster-home/bin/deployall"
      out = File.open(script, "w")
      out.puts "#!/bin/bash"
      out.puts "# Install services into /etc directories"
      out.puts "THOME=`dirname $0`/../.."
      out.puts "cd $THOME"
      priority=80
      @services.each { |svc|
        svcname = File.basename svc
        out.puts get_svc_command("ln -fs $PWD/" + svc + " /etc/init.d/t" + svcname)
        if Configurator.instance.distro?() == OS_DISTRO_REDHAT
          out.puts get_svc_command("/sbin/chkconfig --add t" + svcname)
        elsif Configurator.instance.distro?() == OS_DISTRO_DEBIAN
          out.puts get_svc_command("update-rc.d t" + svcname + " defaults  #{priority}")
          priority=priority+1
        end
      }
      out.puts "# AUTO-CONFIGURED: #{DateTime.now}"
      out.chmod(0755)
      out.close
      info "GENERATED FILE: " + script
    end
  end

  def write_undeployall
    # Create undeployall script.
    if Configurator.instance.can_install_services_on_os?()
      script = "#{get_deployment_basedir()}/cluster-home/bin/undeployall"
      out = File.open(script, "w")
      out.puts "#!/bin/bash"
      out.puts "# Remove services from /etc directories"
      out.puts "THOME=`dirname $0`/../.."
      out.puts "cd $THOME"
      @services.each { |svc|
        svcname = File.basename svc
        if Configurator.instance.distro?() == OS_DISTRO_REDHAT
          out.puts get_svc_command("/sbin/chkconfig --del t" + svcname)
          out.puts get_svc_command("rm -f /etc/init.d/t" + svcname)
        elsif Configurator.instance.distro?() == OS_DISTRO_DEBIAN
          out.puts get_svc_command("rm -f /etc/init.d/t" + svcname)
          out.puts get_svc_command("update-rc.d -f  t" + svcname + " remove")
        end
      }
      out.puts "# AUTO-CONFIGURED: #{DateTime.now}"
      out.chmod(0755)
      out.close
      info "GENERATED FILE: " + script
    end
  end
  
  def stop_replication_services
    unless get_additional_property(ACTIVE_DIRECTORY_PATH)
      return
    end
    
    super()
  end
  
  def start_replication_services
    if get_additional_property(ACTIVE_DIRECTORY_PATH) && get_additional_property(REPLICATOR_ENABLED) == "true"
      super()
    elsif @config.getProperty(SVC_START) == "true"
      if is_manager?() && manager_is_running?() != true
        info("Starting the manager")
        info(cmd_result("#{@config.getProperty(CURRENT_RELEASE_DIRECTORY)}/tungsten-manager/bin/manager start"))
      end

      if is_replicator?() && replicator_is_running?() != true
        info("Starting the replicator")
        info(cmd_result("#{@config.getProperty(CURRENT_RELEASE_DIRECTORY)}/tungsten-replicator/bin/replicator start"))
      end
    end
  end
  
  def update_metadata
    if get_additional_property(ACTIVE_VERSION) =~ /^1.5.[0-9][\-0-9]+$/
      upgrade_from_1_5()
    end
  end
  
  def upgrade_from_1_5
    if is_replicator?()
      begin
        thl_directory = @config.getProperty(get_host_key(REPL_LOG_DIR))
        service_thl_directory = nil
        target_dynamic_properties = nil

        @config.getPropertyOr([REPL_SERVICES], {}).each_key{
          |rs_alias|
          
          service_thl_directory = @config.getProperty([REPL_SERVICES, rs_alias, REPL_LOG_DIR])
          target_dynamic_properties = @config.getProperty([REPL_SERVICES, rs_alias, REPL_SVC_DYNAMIC_CONFIG])
        }

        if service_thl_directory == nil
          warning("Unable to upgrade the replicator to the current version")
          raise IgnoreError
        end
        
        source_replicator_properties = "#{get_additional_property(ACTIVE_DIRECTORY_PATH)}/tungsten-replicator/conf/dynamic.properties"
        if File.exists?(source_replicator_properties)
          info("Upgrade the previous replicator dynamic properties")
          FileUtils.mv(source_replicator_properties, target_dynamic_properties)
        end
        
        Dir[thl_directory + "/thl.*"].sort().each do |file| 
          FileUtils.mv(file, service_thl_directory)
        end
      rescue IgnoreError
      end
      
      @config.getPropertyOr([REPL_SERVICES], {}).each_key{
        |rs_alias|
        
        begin
          create_tungsten_schema(rs_alias)
          
          applier = get_applier_datasource(rs_alias)
          if applier.is_a?(MySQLDatabasePlatform)
            applier.run("SET SQL_LOG_BIN=0; INSERT INTO #{@config.getProperty([REPL_SERVICES, rs_alias, REPL_SVC_SCHEMA])}.trep_commit_seqno (seqno, fragno, last_frag, source_id, epoch_number, eventid, applied_latency, update_timestamp, extract_timestamp) SELECT seqno, fragno, last_frag, source_id, epoch_number, eventid, applied_latency, update_timestamp, update_timestamp FROM tungsten.trep_commit_seqno")
          elsif applier.is_a?(PGDatabasePlatform)
            # Nothing to do here
          end
        rescue => e
          warning(e.message)
        end
      }
    end
  end
end