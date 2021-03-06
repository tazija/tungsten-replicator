class TungstenTopology
  DATASERVICE = "dataservice"
  COORDINATOR = "coordinator"
  ROUTERS = "routers"
  DATASOURCES = "datasources"
  REPLICATORS = "replicators"
  MANAGER = "manager"
  REPLICATOR = "replicator"
  MASTER = "master"
  DATASERVER = "dataserver"
  STATUS = "status"
  HOSTNAME = "hostname"
  ROLE = "role"
  SEQNO = "seqno"
  LATENCY = "latency"
  
  SERVICE_TYPE = "service_type"
  REPLICATION_SERVICE = :replication
  PHYSICAL_SERVICE = :physical
  COMPOSITE_SERVICE = :composite

  def initialize(install, dataservice = nil)
    @install = install
    @dataservice = dataservice
    @service_type = nil
    @props = nil
  end

  def parse
    if @props != nil
      return
    end
    
    if @dataservice == nil
      if @install.dataservices().size > 1
        raise "Unable to parse trepctl because there are multiple dataservices defined for this replicator. Try specifying a specific replication service."
      end
      @dataservice = @install.dataservices()[0]
    end
    
    if @install.is_manager?()
      parse_manager()
    else
      parse_replicator()
    end
  end
  
  def parse_manager
    @props = Properties.new()
    result = @install.manager_api_result("status/#{@dataservice}", ["serviceState"])

    @props.setProperty(DATASERVICE, @dataservice)
    if result["composite"] == true
      @props.setProperty(SERVICE_TYPE, COMPOSITE_SERVICE)
    else
      @props.setProperty(SERVICE_TYPE, PHYSICAL_SERVICE)
    end
    @props.setProperty(COORDINATOR, {
      "host" => result["coordinator"],
      "mode" => result["policyManagerMode"]
    })
    @props.setProperty(DATASOURCES, result["dataSources"])
    @props.setProperty(REPLICATORS, result["replicators"])
  end
  
  def parse_replicator
    @props = Properties.new()

    @props.setProperty(DATASERVICE, @dataservice)
    @props.setProperty(SERVICE_TYPE, REPLICATION_SERVICE)
    r_props = {}
    TU.cmd_result("#{@install.trepctl(@dataservice)} status | grep :").each{
      |line|
      parts = line.split(":")
      key = parts.shift()
      r_props[key.strip()] = parts.join(":").strip()
    }
    @props.setProperty([REPLICATORS, @install.hostname()], r_props)
  end
  
  def dataservice
    self.parse()
    return @props.getProperty(DATASERVICE)
  end
  
  def coordinator
    self.parse()
    return @props.getProperty(['coordinator','host'])
  end
  
  def policy
    self.parse()
    return @props.getProperty(['coordinator','mode'])
  end
  
  def datasources
    self.parse()
    return @props.getPropertyOr([DATASOURCES], {}).keys()
  end
  
  def replicators
    self.parse()
    return @props.getPropertyOr([REPLICATORS], {}).keys()
  end
  
  def datasource_role(hostname)
    self.parse()
    return @props.getProperty([DATASOURCES, hostname, 'role'])
  end
  
  def datasource_status(hostname)
    self.parse()
    return @props.getProperty([DATASOURCES, hostname, 'state'])
  end
  
  def replicator_role(hostname)
    self.parse()
    return @props.getProperty([REPLICATORS, hostname, 'role'])
  end
  
  def replicator_status(hostname)
    self.parse()
    return @props.getProperty([REPLICATORS, hostname, 'state'])
  end
  
  def datasource_latency(hostname)
    self.parse()
    return @props.getProperty([REPLICATORS, hostname, 'appliedLatency']).to_f()
  end
  
  def is_replication?
    self.parse()
    return @props.getProperty(SERVICE_TYPE) == REPLICATION_SERVICE
  end
  
  def is_physical?
    self.parse()
    return @props.getProperty(SERVICE_TYPE) == PHYSICAL_SERVICE
  end
  
  def is_composite?
    self.parse()
    return @props.getProperty(SERVICE_TYPE) == COMPOSITE_SERVICE
  end
  
  def to_s
    self.parse()
    return @props.to_s()
  end

  def to_a
    self.parse()
    return @props.props
  end

  def output()
    self.parse()
    TU.output(self.to_s)
  end

  def force_output()
    self.parse()
    TU.force_output(self.to_s)
  end
end