replicator.applier.dbms=com.continuent.tungsten.replicator.applier.NuoDBApplier
replicator.applier.nuodb.database=@{APPLIER.REPL_NUODB_DATABASE}
replicator.applier.nuodb.schema=@{APPLIER.REPL_NUODB_SCHEMA}

replicator.applier.dbms.host=${replicator.global.db.host}
replicator.applier.dbms.database=${replicator.applier.nuodb.database}
replicator.applier.dbms.port=${replicator.global.db.port}
replicator.applier.dbms.user=${replicator.global.db.user}
replicator.applier.dbms.password=${replicator.global.db.password}
replicator.applier.dbms.schema=${replicator.applier.nuodb.schema}
#replicator.applier.dbms.parameters=