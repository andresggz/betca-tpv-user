http://localhost:8080/api/v0/actuator
> Path base, por defecto.

health
> Shows application health information.

info
> Displays arbitrary application info.

`application.properties`   
info.app.artifact=@project.artifactId@   
info.app.version=@project.version@   
info.app.build=@maven.build.timestamp@   

httptrace
> Displays HTTP trace information (by default, the last 100 HTTP request-response exchanges). Requires an HttpTraceRepository bean.

mappings
> Displays a collated list of all @RequestMapping paths.