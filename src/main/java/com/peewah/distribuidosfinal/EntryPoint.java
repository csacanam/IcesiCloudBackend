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
import com.peewah.models.SistemaOperativo;
import com.peewah.models.Usuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.setPort;

/**
 *
 * @author csacanam
 */
public class EntryPoint
{

    //Constantes
    private static final String SESSION_NAME = "username";

    //Dao
    private static Dao<Usuario, String> usuarioDao;
    private static Dao<SistemaOperativo, String> sistemaOperativoDao;
    private static Dao<MaquinaVirtual, Integer> maquinaVirtualDao;
    private static Dao<App, String> appDao;
    private static Dao<MaquinaApp, String> maquinaAppDao;
    private static Dao<Cookbook, String> cookbookDao;
    private static Dao<CookbookApp, String> cookbookAppDao;

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

        } catch (SQLException ex)
        {
            System.out.println("Error creando las tablas");
        }

        //4. Asignación de puerto
        ProcessBuilder process = new ProcessBuilder();
        Integer port;
        if (process.environment().get("PORT") != null)
        {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else
        {
            port = 8080;
        }
        setPort(port);

        //5. Habilitar Cross-origin resource sharing (CORS)
        options(new Route("/*")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
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

            }

        });

        before(new Filter()
        {

            @Override
            public void handle(Request rqst, Response rspns)
            {
                rspns.header("Access-Control-Allow-Origin", "*");

            }
        });

        //6. Web services
        //Crear usuario
        post(new Route("/new-user")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
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
            }

        });

        //Autenticar usuario
        post(new Route("/auth-user")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
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
                        if (usuario.getPassword().equals(password))
                        {
                            rqst.session().attribute(SESSION_NAME, username);
                            return true;
                        }
                    } catch (SQLException ex)
                    {
                        return false;
                    }

                } else
                {
                    return false;
                }

                return false;
            }

        });

        //Cerrar sesión
        get(new Route("/logout")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
            {
                rqst.session().removeAttribute(SESSION_NAME);

                return null;
            }

        });

        //Listar sistemas operativos disponibles
        get(new Route("/list-so")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
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
            }

        });

        //Crear máquina virtual
        post(new Route("/create-machine")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
            {

                String username = rqst.session().attribute(SESSION_NAME);

                if (username != null && !username.equals(""))
                {
                    try
                    {
                        //Obtener parámetros
                        String nombreMaquina = rqst.queryParams("nombreMaquina");
                        String nombreSO = rqst.queryParams("nombreSO");

                        //Crear máquina virtual
                        MaquinaVirtual maquinaVirtual = new MaquinaVirtual();
                        maquinaVirtual.setNombre(nombreMaquina);
                        maquinaVirtual.setSistemaOperativo(sistemaOperativoDao.queryForId(nombreSO));
                        maquinaVirtual.setUsername(usuarioDao.queryForId(username));

                        maquinaVirtualDao.create(maquinaVirtual);

                        return true;

                    } catch (SQLException ex)
                    {
                        System.out.println("Error creando la máquina virtual");
                        return false;
                    }
                } else
                {
                    System.out.println("Error con la autenticación del usuario");

                    return false;
                }

            }

        });

        //Eliminar usuario y sus máquinas virtuales asociadas
        delete(new Route("/delete-user")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
            {
                String nombreUsuario = rqst.session().attribute(SESSION_NAME);

                if (nombreUsuario != null && nombreUsuario.equals("admin"))
                {
                    try
                    {
                        Usuario user = usuarioDao.queryForId(nombreUsuario);

                        //Eliminar máquinas virtuales del usuario
                        Collection<MaquinaVirtual> maquinasUsuario = user.getMaquinasVirtuales();
                        for (MaquinaVirtual maquina : maquinasUsuario)
                        {
                            maquinaVirtualDao.delete(maquina);
                        }

                        //Eliminar usuario
                        usuarioDao.delete(user);

                        return true;
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

            }

        });

        //Listar máquinas virtuales de un usuario
        get(new Route("/list-machines")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
            {
                String username = rqst.session().attribute(SESSION_NAME);

                if (username != null && !username.equals(""))
                {
                    List<MaquinaVirtual> maquinas = new ArrayList<>();

                    try
                    {

                        QueryBuilder<MaquinaVirtual, Integer> queryBuilder = maquinaVirtualDao.queryBuilder();
                        queryBuilder.where().eq(MaquinaVirtual.USERNAME_FIELD, username);

                        PreparedQuery<MaquinaVirtual> preparedQuery = queryBuilder.prepare();

                        maquinas = maquinaVirtualDao.query(preparedQuery);

                    } catch (SQLException ex)
                    {
                        System.out.println("Error consultando las máquinas del usuario");
                    }

                    return maquinas.toString();
                } else
                {
                    return null;
                }

            }

        });

        // Cargar datos de prueba
        get(new Route("/add-testdata")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
            {

                try
                {
                    testData();
                } catch (SQLException ex)
                {
                    return "Error agregando información de prueba";
                }

                return "OK";

            }

        });

        get(new Route("/delete-testdata")
        {

            @Override
            public Object handle(Request rqst, Response rspns)
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
                } catch (SQLException ex)
                {
                    return "Error eliminando la información";
                }

                return "OK";

            }

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

    }

}
