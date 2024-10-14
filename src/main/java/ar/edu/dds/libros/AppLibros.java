package ar.edu.dds.libros;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.net.URI;
import java.net.URISyntaxException;


public class AppLibros {

	public static EntityManagerFactory entityManagerFactory;

	public static void main(String[] args) throws Exception {
		
		Map<String, String> env = System.getenv();
		for (String string : env.keySet()) {
			System.out.println(string + ": " + env.get(string));
		}
		
		entityManagerFactory =  createEntityManagerFactory();
		String strport = System.getenv("PORT");
		if (strport == null){
			strport = "8080";
		}
		Integer port = Integer.parseInt(strport);

		Javalin app = Javalin.create().start(port);
		
		LibrosController controller = new LibrosController(entityManagerFactory); 
		
		app.get("/libros", controller::listLibros);
		app.post("/libros", controller::addLibro);
		
	}
	
	
	
	public static EntityManagerFactory createEntityManagerFactory() throws Exception {
	    Map<String, String> env = System.getenv();
	    Map<String, Object> configOverrides = new HashMap<String, Object>();
	    String[] keys = new String[] { 
	        "javax__persistence__jdbc__driver",
	        "javax__persistence__jdbc__password",
	        "javax__persistence__jdbc__url",
	        "javax__persistence__jdbc__user",
	        "hibernate__hbm2ddl__auto",
	        "hibernate__connection__pool_size", 
	        "hibernate__show_sql",
	        "hibernate__dialect"
	    };
	
	    for (String key : keys) {
	        try {
	            String key2 = key.replace("__",".");
	            if (env.containsKey(key)) {
	                String value = env.get(key);
	                configOverrides.put(key2, value);
	            }
	        } catch(Exception ex){
	            System.out.println("Error configurando " + key + ": " + ex.getMessage());    
	        }
	    }
	
	    // Asegurar que el dialecto esté configurado
	    if (!configOverrides.containsKey("hibernate.dialect")) {
	        configOverrides.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
	    }
	
	    System.out.println("Config overrides ----------------------");
	    for (String key : configOverrides.keySet()) {
	        System.out.println(key + ": " + configOverrides.get(key));
	    }
	
	    // Verificar configuraciones críticas
	    String[] criticalKeys = {"javax.persistence.jdbc.url", "javax.persistence.jdbc.driver", "hibernate.dialect"};
	    for (String key : criticalKeys) {
	        if (!configOverrides.containsKey(key)) {
	            throw new Exception("Configuración crítica faltante: " + key);
	        }
	    }
	
	    return Persistence.createEntityManagerFactory("db", configOverrides);
	}
}
