require 'tempfile'

DBMS_NUODB = "nuodb"

REPL_NUODB_DATABASE = "repl_nuodb_database"
REPL_NUODB_SCHEMA = "repl_nuodb_schema"

class NuoDBDatabasePlatform < ConfigureDatabasePlatform
  def initialize(host, port, username, password, config, ds_alias)
    super(host, port, username, password, config, ds_alias)
  end

  def get_uri_scheme
    DBMS_NUODB
  end

  def get_default_backup_method
    "none"
  end

  def get_valid_backup_methods
    "none|script"
  end

  def get_database
    @config.getProperty(REPL_NUODB_DATABASE)
  end

  def get_thl_uri
    "jdbc:com.nuodb://${replicator.global.db.host}:${replicator.global.db.port}/${replicator.applier.dbms.database}?schema=${replicator.schema}"
  end

  def check_thl_schema(thl_schema)
    schemas = run("SHOW SCHEMA \"#{thl_schema}\"")
    if schemas != ""
      raise "THL schema #{thl_schema} already exists at #{get_connection_summary()}"
    end
  end

  def get_schema
    @config.getProperty(REPL_NUODB_SCHEMA)
  end

  # Execute NuoSQL command and return result to client
  def run(command)
    begin
      exec = "nuosql \"#{self.get_database()}@#{@host}:#{@port}\" --user \"#{username}\" --password \"#{@password}\" --schema \"#{self.get_schema()}\""

      tmp = Tempfile.new('options')
      tmp << "#{exec}"
      tmp.flush

      return cmd_result("#{exec} < #{command}")
    rescue CommandError
      return nil
    end
  end

  def get_default_port
    "48004"
  end

  def get_default_start_script
    nil
  end

  def get_extractor_template
    raise "NuoDB extractor is not yet implemented"
  end

  def get_applier_filters()
    []
  end

  def get_default_master_log_directory
    nil
  end

  def get_default_master_log_pattern
    nil
  end

  def getBasicJdbcUrl()
    "jdbc:com.nuodb://${replicator.global.db.host}:${replicator.global.db.port}"
  end

  def getJdbcUrl()
    "jdbc:com.nuodb://${replicator.global.db.host}:${replicator.global.db.port}/${replicator.applier.dbms.database}"
  end

  def getJdbcDriver()
    "com.nuodb.jdbc.Driver"
  end

  def getVendor()
    "nuodb"
  end
end

#
# Prompts
#
class NuoDBConfigurePrompt < ConfigurePrompt
  def get_default_value
    begin
      if Configurator.instance.display_help? && !Configurator.instance.display_preview?
        raise ""
      end

      get_nuodb_default_value()
    rescue => e
      super()
    end
  end

  def get_nuodb_default_value
    raise "Undefined function"
  end

  def enabled?
    super() && (get_datasource().is_a?(NuoDBDatabasePlatform))
  end

  def enabled_for_config?
    super() && (get_datasource().is_a?(NuoDBDatabasePlatform))
  end
end

#
# Database name prompt
#
class NuoDBDatabasePrompt < NuoDBConfigurePrompt
  include DatasourcePrompt

  def initialize
    super(REPL_NUODB_DATABASE, "NuoDB database name to replicate into", PV_ANY)
  end
end

#
# Database name prompt
#
class NuoDBSchemaPrompt < NuoDBConfigurePrompt
  include DatasourcePrompt

  def initialize
    super(REPL_NUODB_SCHEMA, "NuoDB schema name to use", PV_ANY)
  end

  def required?
    false
  end
end