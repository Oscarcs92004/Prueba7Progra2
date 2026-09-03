/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Scanner;
/**
 *
 * @author oscar
 */
public class EmpleadoMain {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        EmpleadoManager manager = new EmpleadoManager();
        int opcion;
        do{
            System.out.println("\n\n==========MENU========== ");
            System.out.println("1. Agregar Empleado");
            System.out.println("2. Listar Empleado(No Despedidos)");
            System.out.println("3. Agregar Venta");
            System.out.println("4. Pagar Empleado");
            System.out.println("5. Despedir Empleado");
            System.out.println("6. Ver Reporte de Empleado");
            System.out.println("0. Salir");
            System.out.println("Escoja una opcion: ");
            opcion = sc.nextInt();
            sc.nextLine();
            try {
                switch (opcion) {
                    case 1:
                        System.out.print("Nombre: ");
                        String nombre = sc.nextLine();
                        System.out.print("Salario base: ");
                        double salario = sc.nextDouble();
                        sc.nextLine();
                        manager.addEmpleado(nombre, salario);
                        System.out.println("Empleado agregado correctamente.");
                        break;
 
                    case 2:
                        System.out.println("\n--- Empleados Activos ---");
                        manager.listarEmpleados();
                        break;
 
                    case 3:
                        System.out.print("Codigo del empleado: ");
                        int codVenta = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Monto de la venta: ");
                        double monto = sc.nextDouble();
                        sc.nextLine();
                        manager.addSaleToEmployee(codVenta, monto);
                        System.out.println("Venta registrada.");
                        break;
 
                    case 4:
                        System.out.print("Codigo del empleado a pagar: ");
                        int codPago = sc.nextInt();
                        sc.nextLine();
                        manager.payEmployee(codPago);
                        break;
 
                    case 5:
                        System.out.print("Codigo del empleado a despedir: ");
                        int codFire = sc.nextInt();
                        sc.nextLine();
                        boolean despedido = manager.fireEmployee(codFire);
                        if (despedido) {
                            System.out.println("Empleado despedido correctamente.");
                        } else {
                            System.out.println("No se pudo despedir: no existe o ya estaba despedido.");
                        }
                        break;
 
                    case 6:
                        System.out.print("Codigo del empleado: ");
                        int codReporte = sc.nextInt();
                        sc.nextLine();
                        System.out.println();
                        manager.printEmployee(codReporte);
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        break;
 
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }
        } while(opcion != 0);
    }
}
