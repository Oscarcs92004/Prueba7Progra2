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
            System.out.println("Listar Empleado(No Despedidos)");
            System.out.println("3. Agregar Venta");
            System.out.println("4. Pagar Empleado");
            System.out.println("5. Despedir Empleado");
            System.out.println("6. Ver Reporte de Empleado");
            System.out.println("0. Salir");
            System.out.println("Escoja una opcion: ");
            opcion = sc.nextInt();
        } while(opcion != 0);
    }
}
