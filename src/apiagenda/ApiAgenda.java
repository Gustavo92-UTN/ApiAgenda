package apiagenda;
/* @Gustavo Apaza Huanca */
import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
public class ApiAgenda {
    static Scanner entrada = new Scanner(System.in);
    Connection conectarBD;
    static Statement st;
    static PreparedStatement ps;
    static ResultSet rs;
    public ApiAgenda(){
        try{
	    Class.forName("com.mysql.cj.jdbc.Driver");
            conectarBD = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda_bd?serverTimezone=UTC","root","12345");
            System.out.println("Se conecto a la Base de datos");
        } catch(Exception e) {
            System.err.println("Error " + e);
        }
    }
    static ApiAgenda objetoApiAgenda = new ApiAgenda();
    
    public static void main(String[] args) {
        int opcion;
        System.out.println(".:: Agenda de Contactos ::.");
        do{
            System.out.print("\nElija una opcion del menu para interactuar con el programa" 
                            + "\n.:: MENU ::."
                            + "\n1. Mostrar contactos"
                            + "\n2. Buscar contacto"
                            + "\n3. Agregar nuevo contacto"
                            + "\n4. Editar contacto"
                            + "\n5. Eliminar contacto"
                            + "\n0. Salir del menu\n"
                            + "\nOpcion del menu: ");
            opcion = entrada.nextInt();
            
            while(opcion < 0 || opcion > 5){
                System.out.print("Opcion Incorrecta! Elija una opcion del menu: ");
                opcion = entrada.nextInt();
            }
            entrada.nextLine();
            
            switch (opcion){
                case 1:
                    mostrarContactos();
                    break;
                case 2:
                    buscarContacto();
                    break;
                case 3:
                    agregarNuevoContacto();
                    break;
                case 4:
                    editarContacto();
                    break;
                case 5:
                    eliminarContacto();
                    break;
            }
            if(opcion == 0){
                try{
                    objetoApiAgenda.conectarBD.close();
                }catch(Exception e){
                    System.err.println("Error al cerrar la conexion con la BD");
                }
                System.out.println("\n================== Fin del programa ==================");
            }
        }while(opcion != 0);
    }
    ////////////////////////////////////////////////////////////////////////////
    // FUNCIONES para mostrar contactos
    ////////////////////////////////////////////////////////////////////////////
    public static void mostrarContactos(){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Lista de Contactos <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
                        + "\nN° de contacto || Nombre || Apellido || Fecha de nacimiento || email || telefono");
        try{
            st = objetoApiAgenda.conectarBD.createStatement();
            rs = st.executeQuery("SELECT * FROM contacto");

            while(rs.next()){
                System.out.println(rs.getInt("id") + " || "
                                + rs.getString("nombre") + " || " 
                                + rs.getString("apellido") + " || " 
                                + rs.getString("fechaDeNacimiento") + " || " 
                                + rs.getString("email") + " || " 
                                + rs.getInt("telefono")
                                + verificarCumple(rs.getString("fechaDeNacimiento")));
            }
        }catch (Exception e){
            System.err.println("ERROR AL OBTENER LOS DATOS");
        }
    }
    
    public static String verificarCumple(String fecha){
        /*
        La fecha que viene desde la BD esta en este formato: YYYY-MM-DD
        */
        String mensaje = "";
        int mesActual, diaActual, mesRegistro, diaRegistro;
        DateTimeFormatter formatoMes = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter formatoDia = DateTimeFormatter.ofPattern("dd");
        /*
        La fecha que captura la funcion LocalDate.now() esta en el formato: YYYY-MM-DD
        de esta fecha solo me interesa el mes y el dia, entoces obtengo esos 2
        datos de la variable fechaActual y los paso a entero
        */
        LocalDate fechaActual = LocalDate.now();
        mesActual = Integer.parseInt(fechaActual.format(formatoMes));
        diaActual = Integer.parseInt(fechaActual.format(formatoDia));
        /*
        Esta funcion recibe como parametro la fecha de un registro como un string,
        por eso lo convierto a LocalDate para luego repetir el mismo proceso 
        anterior que la fechaActual
        */
        LocalDate fechaRegistro = LocalDate.parse(fecha);
        mesRegistro = Integer.parseInt(fechaRegistro.format(formatoMes));
        diaRegistro = Integer.parseInt(fechaRegistro.format(formatoDia));

        if(mesActual == mesRegistro && diaActual == diaRegistro){
            mensaje = " || Feliz cumpleaños";
        }
        return mensaje;
    }
    ////////////////////////////////////////////////////////////////////////////
    // FUNCION para buscar un contacto por nombre o apellido
    ////////////////////////////////////////////////////////////////////////////
    public static void buscarContacto(){
        String cadena = "";
        int opcion, contador = 0;
        System.out.println("Elija de la lista la forma de buscar el contacto");
        System.out.print("1. Por el nombre"
                    + "\n2. Por el apellido"
                    + "\n\nOpcion de la lista: ");
        opcion = entrada.nextInt();
        
        while(opcion < 1 || opcion > 2){
            System.out.print("\nEleccion Incorrecta! Elija la opcion 1 o 2"
                    + "\nOpcion de la lista: ");
            opcion = entrada.nextInt();
        }
        entrada.nextLine();
        
        switch (opcion){
            case 1:
                System.out.print("Ingrese el nombre o parte de el para empezar a buscar: ");
                cadena = entrada.nextLine().toLowerCase();

                try{
                    String query = "SELECT * FROM contacto WHERE nombre LIKE ?";
                    ps = objetoApiAgenda.conectarBD.prepareStatement(query);
                    ps.setString(1, "%" + cadena + "%");
                    rs = ps.executeQuery();

                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Lista de Contactos <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
                                    + "\nN° de contacto || Nombre || Apellido || Fecha de nacimiento || email || telefono");
                    while(rs.next()){
                        System.out.println(rs.getInt("id") + " || "
                                        + rs.getString("nombre") + " || " 
                                        + rs.getString("apellido") + " || " 
                                        + rs.getString("fechaDeNacimiento") + " || " 
                                        + rs.getString("email") + " || " 
                                        + rs.getInt("telefono")
                                        + verificarCumple(rs.getString("fechaDeNacimiento")));
                        contador++;
                    }

                    if(contador != 0){
                        System.out.println("\nSe encontraron estos resultados");
                    }else{
                        System.out.println("\nNo se encontro ningun resultado segun su busqueda");
                    }
                }catch (SQLException e){
                    System.err.println("ERROR AL OBTENER LOS DATOS " + e);
                }
                break;
            case 2:
                System.out.print("Ingrese el apellido o parte de el para empezar a buscar: ");
                cadena = entrada.nextLine().toLowerCase();

                try{
                    String query = "SELECT * FROM contacto WHERE apellido LIKE ?";
                    ps = objetoApiAgenda.conectarBD.prepareStatement(query);
                    ps.setString(1, "%" + cadena + "%");
                    rs = ps.executeQuery();

                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Lista de Contactos <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
                                    + "\nN° de contacto || Nombre || Apellido || Fecha de nacimiento || email || telefono");
                    while(rs.next()){
                        System.out.println(rs.getInt("id") + " || "
                                        + rs.getString("nombre") + " || " 
                                        + rs.getString("apellido") + " || " 
                                        + rs.getString("fechaDeNacimiento") + " || " 
                                        + rs.getString("email") + " || " 
                                        + rs.getInt("telefono")
                                        + verificarCumple(rs.getString("fechaDeNacimiento")));
                        contador++;
                    }

                    if(contador != 0){
                        System.out.println("\nSe encontraron estos resultados");
                    }else{
                        System.out.println("\nNo se encontro ningun resultado segun su busqueda");
                    }
                }catch (SQLException e){
                    System.err.println("ERROR AL OBTENER LOS DATOS " + e);
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    // FUNCIONES para agregar nuevo contacto
    ////////////////////////////////////////////////////////////////////////////
    public static void agregarNuevoContacto(){
        String nombre, apellido, email, fechaDeNacimiento;
        int anioNac, mesNac, diaNac, telefono, contadorDeFilas = 0;
        System.out.println("Ingrese los datos del nuevo contacto");
        System.out.print("Nombre: ");
        nombre = entrada.nextLine().toLowerCase();
        System.out.print("Apellido: ");
        apellido = entrada.nextLine().toLowerCase();
        do{
            System.out.print("Anio de nacimiento (en digitos con formato AAAA): ");
            anioNac = entrada.nextInt();
            System.out.print("Mes de nacimiento (en digitos con formato MM): ");
            mesNac = entrada.nextInt();
            System.out.print("Dia de nacimiento (en digitos con formato DD): ");
            diaNac = entrada.nextInt();
            
            if(esFechaIncorrecta(anioNac, mesNac, diaNac)){
                System.out.println("\nFecha incorrecta! Ingrese otra vez los datos");
            }
        }while(esFechaIncorrecta(anioNac, mesNac, diaNac));
        entrada.nextLine();
        /*
        Convierto en String los datos de la fecha de nacimiento
        */
        fechaDeNacimiento = String.valueOf(anioNac) + "-" + String.valueOf(mesNac) + "-" + String.valueOf(diaNac);
        
        do{
            System.out.print("Email: ");
            email = entrada.nextLine().toLowerCase();
            if(esEmailIncorrecto(email)){
                System.out.println("\nEmail incorrecto! Ingrese otra vez el dato");
            }
        }while(esEmailIncorrecto(email));
        
        do{
            System.out.print("Telefono o celular: ");
            telefono = entrada.nextInt();
            if(esNumeroIncorrecto(telefono)){
                System.out.println("\nNumero incorrecto! Ingrese otra vez el dato");
            }
        }while(esNumeroIncorrecto(telefono));
        
        try{
            String query = "insert into contacto(nombre,apellido,fechaDeNacimiento,email,telefono)" + "values (?,?,?,?,?)";          
            ps = objetoApiAgenda.conectarBD.prepareStatement(query);
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, fechaDeNacimiento);
            ps.setString(4, email);
            ps.setInt(5, telefono);
            contadorDeFilas = ps.executeUpdate();

            if(contadorDeFilas > 0 ){
                System.out.println("Se registro el nuevo contacto exitosamente");
            }
        }catch(Exception error){
            System.err.println("Error al ingresar los datos");
        }
    }
    
    public static boolean esFechaIncorrecta(int anio, int mes, int dia){
        int anioActual, mesActual, diaActual, fecha;
        /*
        Si uso en el DateTimeFormatter ... "YYYYMMDD"
        DD tomara 7 de junio = a 158
        pero si cambio DD por dd entonces 7 de junio es = a 7
        */
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("YYYYMMdd");
        LocalDate fechaActual = LocalDate.now();
        fecha = Integer.parseInt(fechaActual.format(formatoFecha));
        anioActual = fecha / 10000;
        mesActual = fecha % 10000 / 100;
        diaActual = fecha % 100;
        
        if((anio < 1900 || anio > anioActual) || (anio == anioActual && ((mes > mesActual) || (dia > diaActual)))){
            return true;
        }else if(mes < 1 || mes > 12){
            return true;
        }else{
            switch(mes){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    if(dia < 1 || dia > 31){
                        return true;
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    if(dia < 1 || dia > 30){
                        return true;
                    }
                    break;
                case 2:
                    if(anio%4 == 0 && (anio%100 != 0 || anio%400 == 0) && (dia < 1 || dia > 29)){
                        return true;
                    }else if(dia < 1 || dia > 28){
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
    
    public static boolean esNumeroIncorrecto(int telefono){
        try{
            if(telefono < 11111111 || telefono > 1599999999){
                return true;
            }
        }catch(Exception e){
            System.out.println("Numero Incorrecto!");
            return true;
        }
        return false;
    }
    
    public static boolean esEmailIncorrecto(String email){
        String expReg = "[^@]+@[^@]+\\.[a-zA-Z]{2,}";
        if(Pattern.matches(expReg, email)){
            return false;
        }
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////
    // FUNCIONES para editar contacto (al menos por uno de sus datos)
    ////////////////////////////////////////////////////////////////////////////
    public static void editarContacto(){
        String nombre, apellido, fechaNacimiento, email;
        int idContacto, opcion, anioNac, mesNac, diaNac, telefono;
        System.out.print("Ingrese la id o el N° de contacto que quiera editar: ");
        idContacto = entrada.nextInt();
        
        do{
            System.out.println("\nElija de la lista que quiere editar de ese contacto"
                + "\n1. Nombre"
                + "\n2. Apellido"
                + "\n3. Fecha de nacimiento"
                + "\n4. Email"
                + "\n5. Telefono"
                + "\n0. Dejar de editar");
            
            System.out.print("\nOpcion de la lista: ");
            opcion = entrada.nextInt();
            while(opcion < 0 || opcion > 5){
                System.out.print("Opcion Incorrecta! Elija una opcion de la lista: ");
                opcion = entrada.nextInt();
            }
            entrada.nextLine();
            switch (opcion){
                case 1:
                    System.out.print("Nuevo nombre: ");
                    nombre = entrada.nextLine().toLowerCase();
                    editarNombre(nombre, idContacto);
                    break;
                case 2:
                    System.out.print("Nuevo apellido: ");
                    apellido = entrada.nextLine().toLowerCase();
                    editarApellido(apellido, idContacto);
                    break;
                case 3:
                    System.out.println("Editando fecha de nacimiento...");
                    do{
                        System.out.print("Nuevo anio (en digitos con formato AAAA): ");
                        anioNac = entrada.nextInt();
                        System.out.print("Nuevo mes (en digitos con formato MM): ");
                        mesNac = entrada.nextInt();
                        System.out.print("Nuevo dia (en digitos con formato DD): ");
                        diaNac = entrada.nextInt();

                        if(esFechaIncorrecta(anioNac, mesNac, diaNac)){
                            System.out.println("\nFecha incorrecta! Ingrese otra vez los datos");
                        }
                    }while(esFechaIncorrecta(anioNac, mesNac, diaNac));
                    entrada.nextLine();
                    fechaNacimiento = String.valueOf(anioNac) + "-" + String.valueOf(mesNac) + "-" + String.valueOf(diaNac);
                    editarFechaDeNacimiento(fechaNacimiento, idContacto);
                    break;
                case 4:
                    do{
                        System.out.print("Nuevo email: ");
                        email = entrada.nextLine().toLowerCase();
                        if(esEmailIncorrecto(email)){
                            System.out.println("\nEmail incorrecto! Ingrese otra vez el dato");
                        }
                    }while(esEmailIncorrecto(email));
                    editarEmail(email, idContacto);
                    break;
                case 5:
                    do{
                        System.out.print("Nuevo telefono o celular: ");
                        telefono = entrada.nextInt();
                        if(esNumeroIncorrecto(telefono)){
                            System.out.println("\nNumero incorrecto! Ingrese otra vez el dato");
                        }
                    }while(esNumeroIncorrecto(telefono));
                    editarTelefono(telefono, idContacto);
                    break;
            }
            if(opcion == 0){
                System.out.println("\nSaliendo de la edicion...");
            }
        }while(opcion != 0);
    }
    
    public static void editarNombre(String nombre, int idContacto){
        try{
            String query = "UPDATE contacto SET nombre = ? WHERE id = ?"; 
            ps = objetoApiAgenda.conectarBD.prepareStatement(query);
            ps.setString(1, nombre);
            ps.setInt(2, idContacto);           
            int resultRowCount = ps.executeUpdate();
            
            if(resultRowCount > 0) {
                mostrarUnContacto(idContacto);
            }else{
                System.out.println("No se modifico ningun registro porque la id ingresada no existe");
            }
        }catch(SQLException e){
            System.out.println("ERROR " + e);
        }
    }
    
    public static void editarApellido(String apellido, int idContacto){
        try{
            String query = "UPDATE contacto SET apellido = ? WHERE id = ?"; 
            ps = objetoApiAgenda.conectarBD.prepareStatement(query);
            ps.setString(1, apellido);
            ps.setInt(2, idContacto);           
            int resultRowCount = ps.executeUpdate();
            
            if(resultRowCount > 0) {
                mostrarUnContacto(idContacto);
            }else{
                System.out.println("No se modifico ningun registro porque la id ingresada no existe");
            }
        }catch(SQLException e){
            System.out.println("ERROR " + e);
        }
    }
    
    public static void editarFechaDeNacimiento(String fechaNacimiento, int idContacto){
        try{
            String query = "UPDATE contacto SET fechaDeNacimiento = ? WHERE id = ?"; 
            ps = objetoApiAgenda.conectarBD.prepareStatement(query);
            ps.setString(1, fechaNacimiento);
            ps.setInt(2, idContacto);           
            int resultRowCount = ps.executeUpdate();
            
            if(resultRowCount > 0) {
                mostrarUnContacto(idContacto);
            }else{
                System.out.println("No se modifico ningun registro porque la id ingresada no existe");
            }
        }catch(SQLException e){
            System.out.println("ERROR " + e);
        }
    }
    
    public static void editarEmail(String email, int idContacto){
        try{
            String query = "UPDATE contacto SET email = ? WHERE id = ?"; 
            ps = objetoApiAgenda.conectarBD.prepareStatement(query);
            ps.setString(1, email);
            ps.setInt(2, idContacto);           
            int resultRowCount = ps.executeUpdate();
            
            if(resultRowCount > 0) {
                mostrarUnContacto(idContacto);
            }else{
                System.out.println("No se modifico ningun registro porque la id ingresada no existe");
            }
        }catch(SQLException e){
            System.out.println("ERROR " + e);
        }
    }
    
    public static void editarTelefono(int telefono, int idContacto){
        try{
            String query = "UPDATE contacto SET telefono = ? WHERE id = ?"; 
            ps = objetoApiAgenda.conectarBD.prepareStatement(query);
            ps.setInt(1, telefono);
            ps.setInt(2, idContacto);           
            int resultRowCount = ps.executeUpdate();
            
            if(resultRowCount > 0) {
                mostrarUnContacto(idContacto);
            }else{
                System.out.println("No se modifico ningun registro porque la id ingresada no existe");
            }
        }catch(SQLException e){
            System.out.println("ERROR " + e);
        }
    }
    
    public static void mostrarUnContacto(int idContacto){
        System.out.println("\n>>> Contacto Actualizado <<<");
        try{
            st = objetoApiAgenda.conectarBD.createStatement();
            rs = st.executeQuery("SELECT * FROM contacto  WHERE id = " + idContacto);

            System.out.println("N° de contacto || Nombre || Apellido || Fecha de nacimiento || email || telefono");
            while(rs.next()){
                System.out.println(rs.getInt("id") + " || "
                                + rs.getString("nombre") + " || " 
                                + rs.getString("apellido") + " || " 
                                + rs.getString("fechaDeNacimiento") + " || " 
                                + rs.getString("email") + " || " 
                                + rs.getInt("telefono")
                                + verificarCumple(rs.getString("fechaDeNacimiento")));
            }
       }catch(SQLException e){
           System.out.println("ERROR " + e);
       }
    }
    ////////////////////////////////////////////////////////////////////////////
    // FUNCION para eliminar un contacto
    ////////////////////////////////////////////////////////////////////////////
    public static void eliminarContacto(){
        int idContacto;
        System.out.print("\nIngrese la id o N° de contacto que quiere eliminar: ");
        idContacto = entrada.nextInt();
        try{
            String query = "DELETE FROM contacto WHERE id = ?";
            ps = objetoApiAgenda.conectarBD.prepareStatement(query);
            ps.setInt(1, idContacto);
            int resultRowCount = ps.executeUpdate();
            
            if(resultRowCount > 0 ){
                System.out.println("El registro se elimino exitosamente");
            }else{
                System.out.println("No se borro ningun registro porque la id ingresada no existe");
            }
        }catch(SQLException e){
            System.out.println("ERROR " + e);
        }
    }
}
