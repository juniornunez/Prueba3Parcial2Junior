/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package binariosejemplo;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Junior Nuñes
 */

public class EmpleadoMain {
    public static void main(String[] args) {
        Scanner lea = new Scanner(System.in);
        EmpleadoManager manager = new EmpleadoManager();
        int opcion;

        do {
            System.out.println("\n*****MENU*****\n");
            System.out.println("1. Agregar Empleado");
            System.out.println("2. Listar Empleado No Despedidos");
            System.out.println("3. Agregar venta al Empleado");
            System.out.println("4. Pagar al empleado");
            System.out.println("5. Despedir al empleado");
            System.out.println("6. Ver detalles del empleado");
            System.out.println("7. Salir");
            System.out.print("Escoja su opcion: ");
            opcion = lea.nextInt();

            switch (opcion) {
                case 1:
                    try {
                        System.out.print("Ingrese el nombre del empleado: ");
                        lea.nextLine(); 
                        String nombre = lea.nextLine();
                        System.out.print("Ingrese el salario del empleado: ");
                        double salario = lea.nextDouble();
                        manager.addEmployee(nombre, salario);
                        System.out.println("Empleado agregado exitosamente.");
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    try {
                        manager.employeeList();
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    try {
                        System.out.print("Ingrese el codigo del empleado: ");
                        int codigo = lea.nextInt();
                        System.out.print("Ingrese el monto de la venta: ");
                        double monto = lea.nextDouble();
                        manager.addSaleToEmployee(codigo, monto);
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 4:
                    try {
                        System.out.print("Ingrese el codigo del empleado: ");
                        int codigo = lea.nextInt();
                        manager.payEmployee(codigo);
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 5:
                    try {
                        System.out.print("Ingrese el codigo del empleado: ");
                        int codigo = lea.nextInt();
                        if (manager.fireEmployee(codigo)) {
                            System.out.println("Empleado despedido exitosamente.");
                        } else {
                            System.out.println("No se pudo despedir al empleado. Verifique el codigo.");
                        }
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 6:
                    try {
                        System.out.print("Ingrese el codigo del empleado: ");
                        int codigo = lea.nextInt();
                        manager.printEmployee(codigo);
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 7:
                    System.out.println("Hasta la proxima soldado");
                    break;

                default:
                    System.out.println("Opcion no valida D:");
                    break;
            }
        } while (opcion != 7);

        lea.close();
    }
}