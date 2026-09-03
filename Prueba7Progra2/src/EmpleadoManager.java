/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
/**
 *
 * @author oscar
 */
public class EmpleadoManager {
    private RandomAccessFile rcods, remps;
    
    private void initCode() throws IOException{
        if(rcods.length() == 0){
            rcods.writeInt(1);
        }
    }
    
    private int getCode() throws IOException{
        rcods.seek(0);
        int codigo = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(codigo+1);
        return codigo;
    }
    
    public EmpleadoManager(){
        try{
            File raiz = new File("Company");
            raiz.mkdir();
            rcods = new RandomAccessFile("Company/codigos.emp","rw");
            remps = new RandomAccessFile("Company/empleados.emp","rw");
            initCode();
        } catch(IOException e){
            
        }
    }
    
    public void addEmpleado(String nombre, double salario)throws IOException{
        remps.seek(remps.length());
        int codigo = getCode();
        remps.writeInt(codigo);
        remps.writeUTF(nombre);
        remps.writeDouble(salario);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        //archivos individuales
        crearFolderEmpleado(codigo);
    }
    
    private String empleadosFolder(int codigo){
        return "Company/empleado"+codigo;
    }
    
    private RandomAccessFile salesFileFor(int codigo) throws IOException{
        String dirPadre = empleadosFolder(codigo);
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        String ruta = dirPadre+"/ventas"+anioActual+".emp";
        return new RandomAccessFile(ruta, "rw");
    }
    /*
        Formato ventasYear.emp
        double ventaMes;
        boolean pago;
        
    */
    private void crearYearSalesFileFor(int codigo)throws IOException{
        RandomAccessFile ryear = salesFileFor(codigo);
        if(ryear.length() == 0){
            for(int mes = 0; mes < 12; mes++){
                ryear.writeDouble(0);
                ryear.writeBoolean(false);
            }
        }
    }
    
    private void crearFolderEmpleado(int codigo) throws IOException{
        File dir = new File(empleadosFolder(codigo));
        dir.mkdir();
        crearYearSalesFileFor(codigo);
    }
    /*
        Codigo - Nombre - Salario - Contratacion
        SOLO LOS QUE NO ESTEN DESPEDIDOS
    */
    public void listarEmpleados()throws IOException{
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            String nombre = remps.readUTF();
            double salario = remps.readDouble();
            Date contratacion = new Date(remps.readLong());
            if (remps.readLong() == 0) {
                System.out.println(codigo + " - " +nombre + " - " +salario + " - " + contratacion);
            }
        }
    }
    
    public boolean isEmpleadoActivo(int codigo) throws IOException{
        remps.seek(0);
        while(remps.getFilePointer() < remps.length()){
            long inicioCampos = remps.getFilePointer(); 
            int codigoAux = remps.readInt();
            remps.readUTF();
            remps.readDouble();
            remps.readLong();
            if(codigoAux == codigo){
                if(remps.readLong() == 0){
                    remps.seek(inicioCampos);
                    remps.readInt();
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    
    public boolean fireEmployee(int codigo) throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            long inicioCampos = remps.getFilePointer(); 
            int codigoAux = remps.readInt();
            remps.readUTF();      
            remps.readDouble();     
            remps.readLong();       
            long posDespedido = remps.getFilePointer(); 
            long despedido = remps.readLong();
 
            if (codigoAux == codigo) {
                if (despedido != 0) {
                    return false; 
                }
                remps.seek(posDespedido);
                remps.writeLong(Calendar.getInstance().getTimeInMillis());
                return true;
            }
        }
        return false; 
    }
    
    public void addSaleToEmployee(int codigo, double monto) throws IOException{
        if(!isEmpleadoActivo(codigo)){
            System.out.println("Empleado no encontrado o no activo.");
            return;
        }
        int mes = Calendar.getInstance().get(Calendar.MONTH);
        long posVentas = (long) mes * 9;
        
        RandomAccessFile ventasEmpleado = salesFileFor(codigo);
        ventasEmpleado.seek(posVentas);
        double ventasActuales = ventasEmpleado.readDouble();
        ventasEmpleado.seek(posVentas);
        ventasEmpleado.writeDouble(ventasActuales + monto);
    }
    
    private RandomAccessFile billsFileFor(int codigo) throws IOException {
        String dirPadre = empleadosFolder(codigo);
        String ruta = dirPadre + "/recibos.emp";
        return new RandomAccessFile(ruta, "rw");
    }
    
    public boolean isEmployeePayed(int codigo) throws IOException {
        int mes = Calendar.getInstance().get(Calendar.MONTH);
        long posMes = (long) mes * 9; 
 
        RandomAccessFile ventasEmpleado = salesFileFor(codigo);
        ventasEmpleado.seek(posMes + 8);
        return ventasEmpleado.readBoolean();
    }
    
    public void payEmployee(int codigo) throws IOException {
        if (!isEmpleadoActivo(codigo)) {
            System.out.println("No se pudo pagar");
            return;
        }
        if (isEmployeePayed(codigo)) {
            System.out.println("No se pudo pagar");
            return;
        }
 
        Calendar cal = Calendar.getInstance();
        int anioActual = cal.get(Calendar.YEAR);
        int mesActual = cal.get(Calendar.MONTH); 
 
        RandomAccessFile ventasEmpleado = salesFileFor(codigo);
        long posMes = (long) mesActual * 9;
        ventasEmpleado.seek(posMes);
        double ventas = ventasEmpleado.readDouble();
 
        String nombre = remps.readUTF();
        double salario = remps.readDouble();
 
        double sueldo = salario + (ventas * 0.10);
        double deduccion = sueldo * 0.035;
        double total = sueldo - deduccion;
 
        RandomAccessFile recibos = billsFileFor(codigo);
        recibos.seek(recibos.length());
        recibos.writeLong(cal.getTimeInMillis());
        recibos.writeDouble(sueldo);
        recibos.writeDouble(deduccion);
        recibos.writeInt(anioActual);
        recibos.writeInt(mesActual);
 
        ventasEmpleado.seek(posMes + 8); 
        ventasEmpleado.writeBoolean(true);
        System.out.println("Empleado "+nombre +" se le pago Lps. " +total);
    }
    
    public void printEmployee(int codigo) throws IOException {
        remps.seek(0);
        boolean encontrado = false;
        String nombre  = "";
        double salario = 0;
 
        while (remps.getFilePointer() < remps.length()) {
            int codigoAux = remps.readInt();
            String nomAux = remps.readUTF();
            double salAux = remps.readDouble();
            long contratacion = remps.readLong();
            long despedido = remps.readLong();
            if (codigoAux == codigo) {
                nombre  = nomAux;
                salario = salAux;
                encontrado = true;
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Salario: " + salario);
                Date fechaCont = new Date(contratacion);
                Calendar calCont = Calendar.getInstance();
                calCont.setTime(fechaCont);
                System.out.println("Fecha de contratacion: "+calCont.get(Calendar.DAY_OF_MONTH)+"/"+(calCont.get(Calendar.MONTH)+1)+"/"+calCont.get(Calendar.YEAR));
                break;
            }
        }
 
        if (!encontrado) {
            System.out.println("Empleado no encontrado.");
            return;
        }
 
        RandomAccessFile rventas = salesFileFor(codigo);
        double totalAnual = 0;
 
        for (int mes = 0; mes < 12; mes++) {
            long posMes = (long) mes * 9;
            rventas.seek(posMes);
            double ventasMes = rventas.readDouble();
            totalAnual += ventasMes;
            System.out.println("Mes " + (mes + 1) + " : " + ventasMes);
        }
 
        System.out.println("Total de ventas del año: " + totalAnual);
 
        RandomAccessFile recibos = billsFileFor(codigo);
        int contador = 0;
        recibos.seek(0);
        while(recibos.getFilePointer() < recibos.length()){
            recibos.readLong();
            recibos.readDouble();
            recibos.readDouble();
            recibos.readInt();
            recibos.readInt();
            contador++;
        }
        System.out.println("Total de pagos realizados: " + contador);
    }
}
