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
    
}
