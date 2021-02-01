package ar.org.cpci.encuesta;

import ar.org.cpci.encuesta.repository.EncuestaFindException;
import ar.org.cpci.encuesta.repository.EncuestaJPARepository;
import ar.org.cpci.encuesta.repository.EncuestaRepository;
import spark.ModelAndView;
import spark.Route;
import spark.TemplateViewRoute;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.Spark.get;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Server {

    private EncuestaRepository repo = null;

    public static void main(String[] args) {
        enableDebugScreen();
        port(4567);
        initRoutes();


    }

    private static EncuestaRepository createEncuestaRepo() {

        // Carga de el/los repositorios y consola

        //---------------------------------------
        //        repo = new EncuestaFileRepository(new File(dirEncuestas));
        //---------------------------------------
        //        try {
        //            repo = new EncuestaJDBCRepository("jdbc:postgresql://localhost/cpci?user=postgres&password=123");
        //        } catch (SQLException throwables) {
        //            throwables.printStackTrace();
        //            System.err.println("No se pudo establecer la conección a la DB");
        //        }
        //---------------------------------------
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("db");
//        EntityManager em = emf.createEntityManager();
//        // Begin----
//        em.getTransaction().begin();
//        return new EncuestaJPARepository(em);
        return new EncuestaRepository() {
            @Override
            public Encuesta findEncuestaByName(String encuestaName) throws EncuestaFindException {
                Encuesta e = new Encuesta();
                e.setNombre(encuestaName);
                Pregunta p = new Pregunta();
                p.setTexto("cuanto es 2 + 2");
                p.getOpciones().add("4");
                p.getOpciones().add("14");
                e.getPreguntas().add(p);

                p = new Pregunta();
                p.setTexto("cuanto es 2 + 3");
                p.getOpciones().add("5");
                p.getOpciones().add("14");
                e.getPreguntas().add(p);
                return e;
            }

            @Override
            public void begin() {

            }

            @Override
            public void end() {

            }
        };
    }

    public static void initRoutes() {

        String projectDir = System.getProperty("user.dir");
        String staticDir = "/src/main/resources/static/";
        staticFiles.externalLocation(projectDir + staticDir);

        ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine();

        get("/encuesta/:name", (request, response) -> {

            //INIT
            EncuestaRepository repo = createEncuestaRepo();
            repo.begin();
            try {
                //INPUT
                String name = request.params("name");
                //Domain
                Map<String, Object> map = new HashMap<>();
                try {
                    Encuesta pepe = repo.findEncuestaByName(name);
                    //OUTPUT

                    map.put("encuesta", pepe);

                } catch (EncuestaFindException ex) {
                    response.redirect("/encuesta");
                }
                return engine.render(new ModelAndView(map, "encuesta"));
            } finally {
                repo.end();
            }
        });

//        get("/encuesta/:id", (request, response) -> {
//
//            //INIT
//            EncuestaRepository repo = createEncuestaRepo();
//            repo.begin();
//            try {
//                //INPUT
//                String name = request.params("name");
//                //Domain
//                Map<String, Object> map = new HashMap<>();
//                try {
//                    Encuesta encuesta = repo.findEncuestaByName(name);
//                    //OUTPUT
//
//                    map.put("encuesta", encuesta);
//
//                } catch (EncuestaFindException ex) {
//                    response.redirect("/encuesta");
//                }
//                return engine.render(new ModelAndView(map, "encuesta.html"));
//            } finally {
//                repo.end();
//            }
//        });

//        get("/encuesta/", ); // Listar
//        post("/encuesta",); // Crear
//        delete("/encuesta/:id", ); //Modificar
//        post("/encuesta/:id", ); // Actualizar

    }


}