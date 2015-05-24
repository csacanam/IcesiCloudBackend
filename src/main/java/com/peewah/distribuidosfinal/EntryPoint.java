/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.peewah.distribuidosfinal;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.peewah.models.App;
import com.peewah.models.Cookbook;
import com.peewah.models.CookbookApp;
import com.peewah.models.MaquinaApp;
import com.peewah.models.MaquinaVirtual;
import com.peewah.models.Nodo;
import com.peewah.models.SistemaOperativo;
import com.peewah.models.Usuario;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;

/**
 *
 * @author csacanam
 */
public class EntryPoint
{

    //Dao
    private static Dao<Usuario, String> usuarioDao;
    private static Dao<SistemaOperativo, String> sistemaOperativoDao;
    private static Dao<MaquinaVirtual, Integer> maquinaVirtualDao;
    private static Dao<App, String> appDao;
    private static Dao<MaquinaApp, String> maquinaAppDao;
    private static Dao<Cookbook, String> cookbookDao;
    private static Dao<CookbookApp, String> cookbookAppDao;
    private static Dao<Nodo, String> nodoDao;

    //ConnectionSource
    private static ConnectionSource connectionSource = null;

    public static void main(String[] args)
    {
        //1. Conexión con la base de datos
        String dbUrl = "jdbc:postgresql://localhost:5432/distribuidosfinal";
        try
        {
            connectionSource = new JdbcConnectionSource(dbUrl);
            ((JdbcConnectionSource) connectionSource).setUsername("csacanam");
            ((JdbcConnectionSource) connectionSource).setPassword("12345678");
        } catch (SQLException ex)
        {
            System.out.println("Error en la conexión a la base de datos");
        }

        // 2. Data Acces Object (DAO) pattern
        usuarioDao = null;
        sistemaOperativoDao = null;
        maquinaVirtualDao = null;
        maquinaAppDao = null;
        appDao = null;
        cookbookDao = null;
        cookbookAppDao = null;
        nodoDao = null;

        if (connectionSource != null)
        {
            try
            {
                usuarioDao = DaoManager.createDao(connectionSource, Usuario.class);
                sistemaOperativoDao = DaoManager.createDao(connectionSource, SistemaOperativo.class);
                maquinaVirtualDao = DaoManager.createDao(connectionSource, MaquinaVirtual.class);
                maquinaAppDao = DaoManager.createDao(connectionSource, MaquinaApp.class);
                appDao = DaoManager.createDao(connectionSource, App.class);
                cookbookDao = DaoManager.createDao(connectionSource, Cookbook.class);
                cookbookAppDao = DaoManager.createDao(connectionSource, CookbookApp.class);
                nodoDao = DaoManager.createDao(connectionSource, Nodo.class);

            } catch (SQLException ex)
            {
                System.out.println("Error en la creación del DAO");
                System.err.println(ex.getMessage());
            }
        }

        // 3. Crear tabla Usuario si no existe
        try
        {
            TableUtils.createTableIfNotExists(connectionSource, Usuario.class);
            TableUtils.createTableIfNotExists(connectionSource, SistemaOperativo.class);
            TableUtils.createTableIfNotExists(connectionSource, MaquinaVirtual.class);
            TableUtils.createTableIfNotExists(connectionSource, App.class);
            TableUtils.createTableIfNotExists(connectionSource, MaquinaApp.class);
            TableUtils.createTableIfNotExists(connectionSource, Cookbook.class);
            TableUtils.createTableIfNotExists(connectionSource, CookbookApp.class);
            TableUtils.createTableIfNotExists(connectionSource, Nodo.class);

        } catch (SQLException ex)
        {
            System.out.println("Error creando las tablas");
        }

        //4. Asignación de puerto
        ProcessBuilder process = new ProcessBuilder();
        Integer puerto;
        if (process.environment().get("PORT") != null)
        {
            puerto = Integer.parseInt(process.environment().get("PORT"));
        } else
        {
            puerto = 8080;
        }
        spark.SparkBase.port(puerto);

        //5. Habilitar Cross-origin resource sharing (CORS)
        options("/*", (Request rqst, Response rspns) ->
        {
            String accessControlRequestHeaders = rqst.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null)
            {
                rspns.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = rqst.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null)
            {
                rspns.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((Request rqst, Response rspns) ->
        {
            rspns.header("Access-Control-Allow-Origin", "*");
        });

        after((Request rqst, Response rspns) ->
        {
            rspns.type("application/json");
        });

        //6. Web services
        //Crear usuario
        post("/new-user", (Request rqst, Response rspns) ->
        {
            //Obtener datos como parámetros
            String nombre = rqst.queryParams("name");
            String username = rqst.queryParams("username");
            String password = rqst.queryParams("password");

            //Validar si no hay datos vacíos
            if (nombre != null && !nombre.equals("") && username != null && !username.equals("") && password != null && !password.equals(""))
            {
                //Crear objeto usuario
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre);
                usuario.setPassword(password);
                usuario.setUsername(username);

                //Crear objeto en base de datos
                try
                {
                    usuarioDao.create(usuario);

                    //Crear carpeta
                    File file = new File("/tmp/" + username);
                    if (!file.exists())
                    {
                        boolean success = file.mkdir();
                        if (!success)
                        {
                            System.out.println("La carpeta no pudo ser creada");
                        }
                    }

                } catch (SQLException ex)
                {
                    System.out.println("Error creando el usuario");
                    return false;
                }

            } else
            {
                System.out.println("No debes dejar campos vacíos");
                return false;
            }

            System.out.println("Usuario creado");
            return true;
        });

        //Autenticar usuario
        post("/auth-user", (Request rqst, Response rspns) ->
        {
            //Obtener datos como parámetros
            String username = rqst.queryParams("username");
            String password = rqst.queryParams("password");

            //Validar si no hay datos vacíos
            if (username != null && !username.equals("") && password != null && !password.equals(""))
            {

                //Validar la dupla usuario-password
                try
                {
                    Usuario usuario = usuarioDao.queryForId(username);
                    if (usuario != null && usuario.getPassword().equals(password))
                    {
                        return true;
                    }

                } catch (SQLException ex)
                {
                }

            }

            return false;

        });

        //Listar sistemas operativos disponibles
        get("/list-so", (Request rqst, Response rspns) ->
        {
            List<SistemaOperativo> sistemasOperativos = new ArrayList<>();
            try
            {
                sistemasOperativos = sistemaOperativoDao.queryForAll();
            } catch (SQLException ex)
            {
                System.out.println("Error listando los sistemas operativos");
            }

            return sistemasOperativos;
        }, new JsonTransformer());

        //Crear máquina virtual
        post("/create-machine", (Request rqst, Response rspns) ->
        {
            try
            {
                //Obtener parámetros
                String username = rqst.queryParams("username");
                String nombreMaquina = rqst.queryParams("nombreMaquina");
                String nombreSO = rqst.queryParams("nombreSO");

                Usuario user = usuarioDao.queryForId(username);
                SistemaOperativo so = sistemaOperativoDao.queryForId(nombreSO);

                if (user != null && so != null)
                {
                    //Crear máquina virtual
                    MaquinaVirtual maquinaVirtual = new MaquinaVirtual();
                    maquinaVirtual.setNombre(nombreMaquina);
                    maquinaVirtual.setSistemaOperativo(sistemaOperativoDao.queryForId(nombreSO));
                    maquinaVirtual.setUsername(usuarioDao.queryForId(username));

                    maquinaVirtualDao.create(maquinaVirtual);

                    //Crear carpeta
                    String path = "/tmp/" + username + "/" + nombreMaquina;
                    File file = new File(path);
                    if (!file.exists())
                    {
                        boolean success = file.mkdir();

                        if (!success)
                        {
                            System.out.println("No se pudo crear la carpeta para la máquina");
                        } else
                        {

                            //Crear Vagrantfile
                            try (
                                    Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+"/Vagrantfile"), "UTF-8")))
                            {
                                writer.write("VAGRANTFILE_API_VERSION = \"2\"\n");
                                writer.write("Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|\n");
                                writer.write("\n");
                                writer.write("end\n");
                            }

                        }
                    }

                    return true;
                } else
                {
                    return false;
                }

            } catch (SQLException ex)
            {
                System.out.println("Error creando la máquina virtual");
                return false;
            }
        });

        //Eliminar usuario y sus máquinas virtuales asociadas
        delete("/delete-user", (Request rqst, Response rspns) ->
        {
            String userLogged = rqst.queryParams("usernameLogged");
            String nombreUsuario = rqst.queryParams("usernameToDelete");

            if (userLogged != null && !userLogged.equals("") && nombreUsuario != null && !userLogged.equals("") && userLogged.equals("admin"))
            {
                try
                {
                    Usuario user = usuarioDao.queryForId(nombreUsuario);

                    if (user != null)
                    {
                        //Eliminar máquinas virtuales del usuario
                        Collection<MaquinaVirtual> maquinasUsuario = user.getMaquinasVirtuales();
                        for (MaquinaVirtual maquina : maquinasUsuario)
                        {
                            //Eliminar apps de las máquinas virtuales del usuario
                            QueryBuilder<MaquinaApp, String> queryBuilder = maquinaAppDao.queryBuilder();
                            queryBuilder.where().eq(MaquinaApp.MACHINE_FIELD, maquina.getId());
                            PreparedQuery<MaquinaApp> preparedQuery = queryBuilder.prepare();

                            Collection<MaquinaApp> maquinasApps = maquinaAppDao.query(preparedQuery);
                            maquinaAppDao.delete(maquinasApps);

                            //Eliminar la máquina virtual
                            maquinaVirtualDao.delete(maquina);
                        }

                        //Eliminar usuario
                        usuarioDao.delete(user);

                        //Eliminar carpeta del usuario
                        FileUtils.deleteDirectory(new File("/tmp/" + nombreUsuario));

                        return true;
                    } else
                    {
                        return false;
                    }

                } catch (SQLException ex)
                {
                    System.out.println("Error eliminando el usuario");
                    return false;
                }

            } else
            {
                System.out.println("No tiene permisos para realizar esta acción");
                return false;
            }
        });

        //Listar máquinas virtuales de un usuario
        get("/list-machines", (Request rqst, Response rspns) ->
        {
            String username = rqst.queryParams("username");

            if (username != null && !username.equals(""))
            {
                try
                {

                    QueryBuilder<MaquinaVirtual, Integer> queryBuilder = maquinaVirtualDao.queryBuilder();
                    queryBuilder.where().eq(MaquinaVirtual.USERNAME_FIELD, username);

                    PreparedQuery<MaquinaVirtual> preparedQuery = queryBuilder.prepare();

                    return maquinaVirtualDao.query(preparedQuery);

                } catch (SQLException ex)
                {
                    System.out.println("Error consultando las máquinas del usuario");

                }

                return "Problema";
            } else
            {
                return "Problema";
            }
        }, new JsonTransformer());

        //Listar usuarios
        get("/list-users", (Request rqst, Response rspns) ->
        {
            String username = rqst.queryParams("usernameLogged");

            if (username.equals("admin"))
            {
                List<Usuario> usuarios = new ArrayList<>();
                try
                {
                    usuarios = usuarioDao.queryForAll();
                } catch (SQLException ex)
                {
                    System.out.println("Error listando los usuarios");
                }

                return usuarios;
            } else
            {
                System.out.println("No tiene permisos para realizar esta acción");
                return "No tiene permisos para realizar esta acción";
            }
        }, new JsonTransformer());

        // Cargar datos de prueba
        get("/add-testdata", (Request rqst, Response rspns) ->
        {
            try
            {
                if (connectionSource != null)
                {
                    TableUtils.createTableIfNotExists(connectionSource, Usuario.class);
                    TableUtils.createTableIfNotExists(connectionSource, SistemaOperativo.class);
                    TableUtils.createTableIfNotExists(connectionSource, MaquinaVirtual.class);
                    TableUtils.createTableIfNotExists(connectionSource, App.class);
                    TableUtils.createTableIfNotExists(connectionSource, MaquinaApp.class);
                    TableUtils.createTableIfNotExists(connectionSource, Cookbook.class);
                    TableUtils.createTableIfNotExists(connectionSource, CookbookApp.class);
                    TableUtils.createTableIfNotExists(connectionSource, Nodo.class);
                }
                testData();
            } catch (SQLException ex)
            {
                return "Error agregando información de prueba";
            }

            return "OK";
        });

        //Eliminar datos de prueba
        get("/delete-testdata", (Request rqst, Response rspns) ->
        {
            try
            {
                TableUtils.dropTable(connectionSource, Usuario.class, true);
                TableUtils.dropTable(connectionSource, MaquinaVirtual.class, true);
                TableUtils.dropTable(connectionSource, SistemaOperativo.class, true);
                TableUtils.dropTable(connectionSource, App.class, true);
                TableUtils.dropTable(connectionSource, Cookbook.class, true);
                TableUtils.dropTable(connectionSource, MaquinaApp.class, true);
                TableUtils.dropTable(connectionSource, CookbookApp.class, true);
                TableUtils.dropTable(connectionSource, Nodo.class, true);
            } catch (SQLException ex)
            {
                return "Error eliminando la información";
            }

            return "OK";
        });

    }

    /**
     * Permite crear toda la información de prueba
     *
     * @throws SQLException Excepción al ejecutar un comando SQL
     */
    public static void testData() throws SQLException
    {
        // 1. Crear usuarios
        Usuario usuarioUno = new Usuario();
        usuarioUno.setUsername("csacanam");
        usuarioUno.setNombre("Camilo Sacanamboy");
        usuarioUno.setPassword("1234");
        usuarioDao.create(usuarioUno);

        Usuario usuarioDos = new Usuario();
        usuarioDos.setUsername("luisacantillo");
        usuarioDos.setNombre("Luisa Cantillo");
        usuarioDos.setPassword("1234");
        usuarioDao.create(usuarioDos);

        Usuario usuarioTres = new Usuario();
        usuarioTres.setUsername("angelicajulio");
        usuarioTres.setNombre("Angélica Julio");
        usuarioTres.setPassword("1234");
        usuarioDao.create(usuarioTres);

        Usuario usuarioCuatro = new Usuario();
        usuarioCuatro.setUsername("admin");
        usuarioCuatro.setPassword("admin");
        usuarioCuatro.setNombre("Administrador");
        usuarioDao.create(usuarioCuatro);

        // 2. Crear sistemas operativos
        SistemaOperativo sistemaCentOS = new SistemaOperativo();
        sistemaCentOS.setNombre("CentOS 6.5");
        sistemaCentOS.setNombreBox("centos_distribuidos");
        sistemaOperativoDao.create(sistemaCentOS);

        SistemaOperativo sistemaUbuntu = new SistemaOperativo();
        sistemaUbuntu.setNombre("Ubuntu 12.04");
        sistemaUbuntu.setNombreBox("precise32");
        sistemaOperativoDao.create(sistemaUbuntu);

        // 3. Crear máquinas virtuales
        MaquinaVirtual maquinaUnoCS = new MaquinaVirtual();
        maquinaUnoCS.setNombre("Maquina Uno");
        maquinaUnoCS.setSistemaOperativo(sistemaCentOS);
        maquinaUnoCS.setUsername(usuarioUno);
        maquinaVirtualDao.createIfNotExists(maquinaUnoCS);

        MaquinaVirtual maquinaDosCS = new MaquinaVirtual();
        maquinaDosCS.setNombre("Maquina Dos");
        maquinaDosCS.setSistemaOperativo(sistemaCentOS);
        maquinaDosCS.setUsername(usuarioUno);
        maquinaVirtualDao.createIfNotExists(maquinaDosCS);

        MaquinaVirtual maquinaTresCS = new MaquinaVirtual();
        maquinaTresCS.setNombre("Maquina Tres");
        maquinaTresCS.setSistemaOperativo(sistemaUbuntu);
        maquinaTresCS.setUsername(usuarioUno);
        maquinaVirtualDao.createIfNotExists(maquinaTresCS);

        MaquinaVirtual maquinaUnoLC = new MaquinaVirtual();
        maquinaUnoLC.setNombre("Maquina Uno");
        maquinaUnoLC.setSistemaOperativo(sistemaCentOS);
        maquinaUnoLC.setUsername(usuarioDos);
        maquinaVirtualDao.createIfNotExists(maquinaUnoLC);

        MaquinaVirtual maquinaDosLC = new MaquinaVirtual();
        maquinaDosLC.setNombre("Maquina Dos");
        maquinaDosLC.setSistemaOperativo(sistemaCentOS);
        maquinaDosLC.setUsername(usuarioDos);
        maquinaVirtualDao.createIfNotExists(maquinaDosLC);

        // 4. Crear apps
        App appUno = new App();
        appUno.setNombre("Oracle");
        appUno.setSistemaOperativo(sistemaCentOS);
        appDao.create(appUno);

        App appDos = new App();
        appDos.setNombre("Clúster MPI");
        appDos.setSistemaOperativo(sistemaUbuntu);
        appDao.create(appDos);

        // 5. Crear cookbooks
        Cookbook cookbookUno = new Cookbook();
        cookbookUno.setRuta("client-mirror");
        cookbookDao.create(cookbookUno);

        Cookbook cookbookDos = new Cookbook();
        cookbookDos.setRuta("client-packages");
        cookbookDao.create(cookbookDos);

        Cookbook cookbookTres = new Cookbook();
        cookbookTres.setRuta("client-ssh");
        cookbookDao.create(cookbookTres);

        Cookbook cookbookCuatro = new Cookbook();
        cookbookCuatro.setRuta("configure-host");
        cookbookDao.create(cookbookCuatro);

        Cookbook cookbookCinco = new Cookbook();
        cookbookCinco.setRuta("install-mpi4py");
        cookbookDao.create(cookbookCinco);

        Cookbook cookbookSeis = new Cookbook();
        cookbookSeis.setRuta("mpi-example");
        cookbookDao.create(cookbookSeis);

        Cookbook cookbookSiete = new Cookbook();
        cookbookSiete.setRuta("server-mirror");
        cookbookDao.create(cookbookSiete);

        Cookbook cookbookOcho = new Cookbook();
        cookbookOcho.setRuta("server-packages");
        cookbookDao.create(cookbookOcho);

        Cookbook cookbookNueve = new Cookbook();
        cookbookNueve.setRuta("server-ssh");
        cookbookDao.create(cookbookNueve);

        Cookbook cookbookDiez = new Cookbook();
        cookbookDiez.setRuta("updatesourceslist");
        cookbookDao.create(cookbookDiez);

        Cookbook cookbookOnce = new Cookbook();
        cookbookOnce.setRuta("oracle");
        cookbookDao.create(cookbookOnce);

        // 6. Crear CookbookApps
        CookbookApp cookbookAppUno = new CookbookApp();
        cookbookAppUno.setApp(appDos);
        cookbookAppUno.setCookbook(cookbookUno);
        cookbookAppDao.create(cookbookAppUno);

        CookbookApp cookbookAppDos = new CookbookApp();
        cookbookAppDos.setApp(appDos);
        cookbookAppDos.setCookbook(cookbookDos);
        cookbookAppDao.create(cookbookAppDos);

        CookbookApp cookbookAppTres = new CookbookApp();
        cookbookAppTres.setApp(appDos);
        cookbookAppTres.setCookbook(cookbookTres);
        cookbookAppDao.create(cookbookAppTres);

        CookbookApp cookbookAppCuatro = new CookbookApp();
        cookbookAppCuatro.setApp(appDos);
        cookbookAppCuatro.setCookbook(cookbookCuatro);
        cookbookAppDao.create(cookbookAppCuatro);

        CookbookApp cookbookAppCinco = new CookbookApp();
        cookbookAppCinco.setApp(appDos);
        cookbookAppCinco.setCookbook(cookbookCinco);
        cookbookAppDao.create(cookbookAppCinco);

        CookbookApp cookbookAppSeis = new CookbookApp();
        cookbookAppSeis.setApp(appDos);
        cookbookAppSeis.setCookbook(cookbookSeis);
        cookbookAppDao.create(cookbookAppSeis);

        CookbookApp cookbookAppSiete = new CookbookApp();
        cookbookAppSiete.setApp(appDos);
        cookbookAppSiete.setCookbook(cookbookSiete);
        cookbookAppDao.create(cookbookAppSiete);

        CookbookApp cookbookAppOcho = new CookbookApp();
        cookbookAppOcho.setApp(appDos);
        cookbookAppOcho.setCookbook(cookbookOcho);
        cookbookAppDao.create(cookbookAppOcho);

        CookbookApp cookbookAppNueve = new CookbookApp();
        cookbookAppNueve.setApp(appDos);
        cookbookAppNueve.setCookbook(cookbookNueve);
        cookbookAppDao.create(cookbookAppNueve);

        CookbookApp cookbookAppDiez = new CookbookApp();
        cookbookAppDiez.setApp(appDos);
        cookbookAppDiez.setCookbook(cookbookDiez);
        cookbookAppDao.create(cookbookAppDiez);

        CookbookApp cookbookAppOnce = new CookbookApp();
        cookbookAppOnce.setApp(appUno);
        cookbookAppOnce.setCookbook(cookbookOnce);
        cookbookAppDao.create(cookbookAppOnce);

        // 7. Crear MaquinaApps
        MaquinaApp maquinaAppUnoCS = new MaquinaApp();
        maquinaAppUnoCS.setMaquina(maquinaUnoCS);
        maquinaAppUnoCS.setApp(appUno);
        maquinaAppDao.create(maquinaAppUnoCS);

        MaquinaApp maquinaAppDosCS = new MaquinaApp();
        maquinaAppDosCS.setMaquina(maquinaDosCS);
        maquinaAppDosCS.setApp(appUno);
        maquinaAppDao.create(maquinaAppDosCS);

        MaquinaApp maquinaAppTresCS = new MaquinaApp();
        maquinaAppTresCS.setMaquina(maquinaTresCS);
        maquinaAppTresCS.setApp(appDos);
        maquinaAppDao.create(maquinaAppTresCS);

        MaquinaApp maquinaAppUnoLC = new MaquinaApp();
        maquinaAppUnoLC.setMaquina(maquinaUnoLC);
        maquinaAppUnoLC.setApp(appUno);
        maquinaAppDao.create(maquinaAppUnoLC);

        MaquinaApp maquinaAppDosLC = new MaquinaApp();
        maquinaAppDosLC.setMaquina(maquinaDosLC);
        maquinaAppDosLC.setApp(appUno);
        maquinaAppDao.create(maquinaAppDosLC);

        // 8. Crear nodos
        Nodo nodoUnoCS = new Nodo();
        nodoUnoCS.setNombre("oracle");
        nodoUnoCS.setIpPrivada("172.40.0.2");
        nodoUnoCS.setIpPublica("192.168.131.28");
        nodoUnoCS.setMascaraRed("255.255.255.0");
        nodoUnoCS.setCantidadMemoria("512");
        nodoUnoCS.setCantidadCPU("1");
        nodoUnoCS.setInterfazPuente("eth0");
        nodoUnoCS.setParametrosJSON("{\"aptmirror\" => {\"server\" => \"192.168.131.254\"}, \"portUno\" => {\"webPort\" => \"8080\"}, \"portDos\" => {\"dbPort\" => \"1521\"}}");
        nodoUnoCS.setMaquinaVirtual(maquinaUnoCS);
        nodoDao.create(nodoUnoCS);

        Nodo nodoDosCS = new Nodo();
        nodoDosCS.setNombre("oracle2");
        nodoDosCS.setIpPrivada("172.40.0.3");
        nodoDosCS.setIpPublica("192.168.131.29");
        nodoDosCS.setMascaraRed("255.255.255.0");
        nodoDosCS.setCantidadMemoria("512");
        nodoDosCS.setCantidadCPU("1");
        nodoDosCS.setInterfazPuente("eth0");
        nodoDosCS.setMaquinaVirtual(maquinaDosCS);
        nodoDosCS.setParametrosJSON("{\"aptmirror\" => {\"server\" => \"192.168.131.254\"}, \"portUno\" => {\"webPort\" => \"8080\"}, \"portDos\" => {\"dbPort\" => \"1521\"}}");
        nodoDao.create(nodoDosCS);

        Nodo nodoTresCS = new Nodo();
        nodoTresCS.setNombre("mpi_master");
        nodoTresCS.setIpPrivada("172.40.0.4");
        nodoTresCS.setIpPublica("192.168.131.28");
        nodoTresCS.setMascaraRed("255.255.255.0");
        nodoTresCS.setCantidadMemoria("512");
        nodoTresCS.setCantidadCPU("1");
        nodoTresCS.setInterfazPuente("eth0");
        nodoTresCS.setParametrosJSON("{\"aptmirror\" => {\"server\" => \"192.168.131.254\"},\"hostconf\" => {\"hostmaster\" => \"headnode\",\"hostname\" => \"headnode\"}}");
        nodoTresCS.setMaquinaVirtual(maquinaTresCS);
        nodoDao.create(nodoTresCS);

        Nodo nodoCuatroCS = new Nodo();
        nodoCuatroCS.setNombre("mpi_node1");
        nodoCuatroCS.setIpPrivada("172.40.0.5");
        nodoCuatroCS.setIpPublica("192.168.131.29");
        nodoCuatroCS.setMascaraRed("255.255.255.0");
        nodoCuatroCS.setCantidadMemoria("512");
        nodoCuatroCS.setCantidadCPU("1");
        nodoCuatroCS.setInterfazPuente("eth0");
        nodoCuatroCS.setParametrosJSON("{\"aptmirror\" => {\"server\" => \"192.168.131.254\"},\"hostconf\" => {\"hostmaster\" => \"headnode\",\"hostname\" => \"node1\"}}");
        nodoCuatroCS.setMaquinaVirtual(maquinaTresCS);
        nodoDao.create(nodoCuatroCS);

    }

}
