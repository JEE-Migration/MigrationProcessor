*Listo ToDo Crear enumeration para tipos de metodos de http usados en Consume (Mauricio)
*ToDo Crear xml conforme al esquema para representar marketplace (Mauricio)
*ToDO Revisar Consumes vs Requires

*Listo Generar codigo para esquema con XJC (Luis)
*Listo Usar esquema para leer xml y modificar procesador para anotar los tipos encontrados (Luis)

public class A{
    @EJB //@Inject
    B b;


     @Consumes({@Consume(injectionName="b")})
     @EJB //@Inject
     B b;

     public void foo(B b){}
     //To
     @Consumes({@Consume(path="/algo" method=HTTMethods.POST microservice="B" type="co.uniandes.app.MyClassC" )})
     //GET si hay retorno y solo hay parametros no primitivos+string
     //POST en cualquier otro caso
     public void foo(B b){
        b.algo();
     }

     @Consumes({
        @Consume(path="/m1" method=HTTMethods.POST microservice="B" type="co.uniandes.app.MyClassC"  ),
        @Consume(path="/m2" method=HTTMethods.POST microservice="B" type="co.uniandes.app.MyClassC" )
        })
     public Integer bar (){
        B b =null;
        b.m1();

        C c =null;
        c.m2();
     }

}

#Para correr:
mvn -Pexec_migration_step_one compile


