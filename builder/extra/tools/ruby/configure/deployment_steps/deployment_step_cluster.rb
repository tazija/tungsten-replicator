module ConfigureDeploymentStepDeployment
  def get_deployment_methods
    [
      ConfigureDeploymentMethod.new("create_release", -40),
      ConfigureDeploymentMethod.new("deploy_config_files", -20),
    ]
  end
  module_function :get_deployment_methods
  
  def create_release
    symlink_source = false
    unless @config.getProperty(HOME_DIRECTORY) == Configurator.instance.get_base_path() || ("#{@config.getProperty(HOME_DIRECTORY)}/releases/#{Configurator.instance.get_basename()}" == Configurator.instance.get_base_path())
      if @config.getProperty(DEPLOY_CURRENT_PACKAGE) == "true"
        package_path = Configurator.instance.get_package_path()
        if @config.getProperty(HOME_DIRECTORY) == get_deployment_basedir()
          destination = get_deployment_basedir()
          mkdir_if_absent(File.dirname(destination))
          symlink_source = false
        else
          destination = "#{@config.getProperty(HOME_DIRECTORY)}/releases"
          mkdir_if_absent(destination)
          symlink_source = "#{destination}/#{File.basename(package_path)}"
        end
        
        debug("Copy #{package_path} to #{destination}")
        cmd_result("cp -rf #{package_path} #{destination}")
      else
        destination = "#{@config.getProperty(HOME_DIRECTORY)}/releases"
        mkdir_if_absent(destination)
        
        debug("Download and unpack #{@config.getProperty(DEPLOY_PACKAGE_URI)}")
        uri = URI::parse(@config.getProperty(DEPLOY_PACKAGE_URI))

        if uri.scheme == "http" || uri.scheme == "https"
          unless @config.getProperty(DEPLOY_PACKAGE_URI) =~ /.tar.gz/
            raise "Only files ending in .tar.gz may be fetched using #{uri.scheme.upcase}"
          end

          package_basename = File.basename(@config.getProperty(DEPLOY_PACKAGE_URI), ".tar.gz")
          unless (File.exists?("#{@config.getProperty(TEMP_DIRECTORY)}/#{package_basename}.tar.gz"))
            cmd_result("cd #{@config.getProperty(TEMP_DIRECTORY)}; wget --no-check-certificate #{@config.getProperty(DEPLOY_PACKAGE_URI)}")
          else
            debug("Using the package already downloaded to #{@config.getProperty(TEMP_DIRECTORY)}/#{package_basename}.tar.gz")
          end

          cmd_result("cd #{destination}; tar zxf #{@config.getProperty(TEMP_DIRECTORY)}/#{package_basename}.tar.gz")
        elsif uri.scheme == "file"
          rsync_cmd = ["rsync"]
      
          unless uri.port
            rsync_cmd << "-aze ssh --delete"
          else
            rsync_cmd << "-aze \"ssh --delete -p #{uri.port}\""
          end
      
          if uri.host != "localhost"
            unless uri.userinfo
              rsync_cmd << "#{uri.host}:#{uri.path}"
            else
              rsync_cmd << "#{uri.userinfo}@#{uri.host}:#{uri.path}"
            end

            rsync_cmd << @config.getProperty(TEMP_DIRECTORY)
        
            cmd_result(rsync_cmd.join(" "))
          else
            unless File.dirname(uri.path) == @config.getProperty(TEMP_DIRECTORY)
              cmd_result("cp #{uri.path} #{@config.getProperty(TEMP_DIRECTORY)}")
            end
          end
        
          package_basename = File.basename(uri.path)
          if package_basename =~ /.tar.gz$/
            package_basename = File.basename(package_basename, ".tar.gz")
          
            cmd_result("cd #{@config.getProperty(HOME_DIRECTORY)}/releases; tar zxf #{@config.getProperty(TEMP_DIRECTORY)}/#{package_basename}.tar.gz")
          elsif package_basename =~ /.tar$/
            package_basename = File.basename(package_basename, ".tar")
          
            cmd_result("cd #{destination}; tar xf #{@config.getProperty(TEMP_DIRECTORY)}/#{package_basename}.tar")
          elsif File.directory?("#{@config.getProperty(TEMP_DIRECTORY)}/#{package_basename}")
            cmd_result("cp -rf #{@config.getProperty(TEMP_DIRECTORY)}/#{package_basename} #{destination}")
          else
            raise "#{package_basename} is not a directory or recognized archive file"
          end
        else
          raise "Unable to download package from #{@config.getProperty(DEPLOY_PACKAGE_URI)}: #{uri.scheme.upcase()} is an unrecognized scheme"
        end
        
        symlink_source = "#{destination}/#{package_basename}"
      end
      
      unless symlink_source == false
        if File.exists?(get_deployment_basedir()) && !File.symlink?(get_deployment_basedir())
          raise "Unable to create the release directory because #{get_deployment_basedir()} is not a symlink"
        end

        debug("Create symlink to #{symlink_source}")
        cmd_result("rm -f #{get_deployment_basedir()}; ln -s #{symlink_source} #{get_deployment_basedir()}")
      end
    end
    
    Configurator.instance.write_header("Building the Tungsten home directory")
    mkdir_if_absent("#{@config.getProperty(HOME_DIRECTORY)}/service-logs")
    mkdir_if_absent("#{@config.getProperty(HOME_DIRECTORY)}/share")
    
    out = File.open(@config.getProperty(DIRECTORY_LOCK_FILE), "w")
    out.puts(@config.getProperty(HOME_DIRECTORY))
    out.close()
    File.chmod(0644, @config.getProperty(DIRECTORY_LOCK_FILE))

    # Create share/env.sh script.
    script = "#{@config.getProperty(HOME_DIRECTORY)}/share/env.sh"
    debug("Generate environment at #{script}")
    out = File.open(script, "w")
    out.puts "# Source this file to set your environment."
    out.puts "export TUNGSTEN_HOME=#{@config.getProperty(HOME_DIRECTORY)}"
    out.puts "export PATH=$TUNGSTEN_HOME/#{Configurator::CURRENT_RELEASE_DIRECTORY}/tungsten-manager/bin:$TUNGSTEN_HOME/#{Configurator::CURRENT_RELEASE_DIRECTORY}/tungsten-replicator/bin:$PATH"
    out.chmod(0755)
    out.close
    
    # Remove any copied config files to keep the deployment directory clean
    FileUtils.rm_f("#{get_deployment_basedir()}/#{Configurator::CLUSTER_CONFIG}")
    FileUtils.rm_f("#{get_deployment_basedir()}/#{Configurator::TEMP_DEPLOY_HOST_CONFIG}")
  end
  
  def deploy_config_files
    config_file = get_deployment_config_file()
    debug("Write #{config_file}")

    host_config = @config.dup()
    host_config.setProperty(DEPLOYMENT_TYPE, nil)
    host_config.setProperty(GLOBAL_DEPLOY_PACKAGE_URI, nil)
    host_config.setProperty(DEPLOY_PACKAGE_URI, nil)
    host_config.setProperty(DEPLOY_CURRENT_PACKAGE, nil)
    
    host_config.store(config_file)
  end
end