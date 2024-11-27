/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package binariosejemplo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

public class EmpleadoManager {
    private RandomAccessFile rcods, remps;

    public EmpleadoManager() {
        try {
            File mf = new File("company");
            mf.mkdir();
            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");
            initCodes();
        } catch (IOException e) {
        }
    }

    private void initCodes() throws IOException {
        if (rcods.length() == 0) {
            rcods.writeInt(1);
        }
    }

    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code + 1);
        return code;
    }

    public void addEmployee(String name, double salary) throws IOException {
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        createEmployeeFolders(code);
        System.out.println("Empleado agregado con codigo: " + code);
    }

    private String employeeFolder(int code) {
        return "company/empleado" + code;
    }

    private void createEmployeeFolders(int code) throws IOException {
        File employeeDir = new File(employeeFolder(code));
        if (!employeeDir.exists()) {
            employeeDir.mkdir();
        }
        createYearSalesFileFor(code);
    }

    private RandomAccessFile salesFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String path = dirPadre + "/ventas" + yearActual + ".emp";
        return new RandomAccessFile(path, "rw");
    }

    private void createYearSalesFileFor(int code) throws IOException {
        RandomAccessFile ryear = salesFileFor(code);
        if (ryear.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                ryear.writeDouble(0);
                ryear.writeBoolean(false);
            }
        }
    }

    public void employeeList() throws IOException {
        remps.seek(0);
        try {
            while (remps.getFilePointer() < remps.length()) {
                int code = remps.readInt();
                String name = remps.readUTF();
                double salary = remps.readDouble();
                Date dateH = new Date(remps.readLong());
                long despido = remps.readLong();
                if (despido == 0) {
                    System.out.println("Codigo: " + code + " Nombre: " + name
                            + " Salario: Lps." + salary + " Contratado: " + dateH);
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo los empleados: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

   private boolean isEmployeeActive(int code) throws IOException {
    remps.seek(0); 
    while (remps.getFilePointer() < remps.length()) {
        int codigo = remps.readInt();
        remps.readUTF();
        remps.skipBytes(16); 
        if (remps.readLong() == 0 && codigo == code) { 
            return true;
        }
    }
    return false;
}

    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            String name = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a: " + name);
            return true;
        }
        return false;
    }

    public void addSaleToEmployee(int code, double monto) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("El empleado con codigo " + code + " no esta activo");
            return;
        }
        RandomAccessFile ventas = salesFileFor(code);
        Calendar calendario = Calendar.getInstance();
        int mesActual = calendario.get(Calendar.MONTH);
        ventas.seek(mesActual * 9);
        double total = ventas.readDouble();
        ventas.seek(mesActual * 9);
        ventas.writeDouble(total + monto);
        System.out.println("Venta de Lps." + monto + " añadida.");
    }

    public void payEmployee(int code) throws IOException {
    if (!isEmployeeActive(code)) {
        System.out.println("El empleado con codigo " + code + " no esta activo.");
        return;
    }
    RandomAccessFile salesFile = salesFileFor(code);
    Calendar calendario = Calendar.getInstance();
    int mes = calendario.get(Calendar.MONTH);
    int anio = calendario.get(Calendar.YEAR);
    salesFile.seek(mes * 9 + 8);
    if (salesFile.readBoolean()) {
        System.out.println("El empleado ya fue pagado este mes.");
        return;
    }
    remps.seek(0);
    while (remps.getFilePointer() < remps.length()) {
        int codigo = remps.readInt();
        String nombre = remps.readUTF(); 
        double salario = remps.readDouble(); 
        long fechaContratacion = remps.readLong(); 
        long fechaDespido = remps.readLong(); 

        if (codigo == code && fechaDespido == 0) {
            salesFile.seek(mes * 9);
            double ventasMes = salesFile.readDouble();
            double comision = ventasMes * 0.10;
            double sueldoBase = salario + comision;
            double deduccion = sueldoBase * 0.035;
            double sueldoNeto = sueldoBase - deduccion;
            salesFile.seek(mes * 9 + 8);
            salesFile.writeBoolean(true); 
            RandomAccessFile recibo = new RandomAccessFile(employeeFolder(code) + "/recibos.emp", "rw");
            recibo.seek(recibo.length());
            recibo.writeLong(new Date().getTime());
            recibo.writeDouble(comision);
            recibo.writeDouble(salario);
            recibo.writeDouble(deduccion);
            recibo.writeDouble(sueldoNeto);
            recibo.writeInt(anio);
            recibo.writeInt(mes + 1);
            System.out.println("Pagado: " + nombre + " Sueldo Neto: Lps." + sueldoNeto);
            return;
        }
    }
    System.out.println("Empleado no encontrado.");
}

    public void printEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("El empleado con codigo " + code + " no esta activo");
            return;
        }
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            if (codigo == code) {
                String nombre = remps.readUTF();
                double salario = remps.readDouble();
                System.out.println("Empleado: " + nombre + " Codigo: " + code + " Salario Base: Lps." + salario);
                RandomAccessFile ventas = salesFileFor(code);
                double totalVentas = 0;
                for (int mes = 0; mes < 12; mes++) {
                    ventas.seek(mes * 9);
                    double ventaMes = ventas.readDouble();
                    totalVentas += ventaMes;
                    System.out.println("Mes " + (mes + 1) + ": Lps." + ventaMes);
                }
                System.out.println("Total Ventas: Lps." + totalVentas);
                String reciboPath = employeeFolder(code) + "/recibos.emp";
                File archivoRecibos = new File(reciboPath);
                if (archivoRecibos.exists()) {
                    RandomAccessFile recibos = new RandomAccessFile(reciboPath, "r");
                    while (recibos.getFilePointer() < recibos.length()) {
                        long fecha = recibos.readLong();
                        double comision = recibos.readDouble();
                        double base = recibos.readDouble();
                        double deduccion = recibos.readDouble();
                        double neto = recibos.readDouble();
                        int anio = recibos.readInt();
                        int mes = recibos.readInt();
                        System.out.println("Recibo - Fecha: " + new Date(fecha) + " Comision: Lps." + comision + " Neto: Lps." + neto);
                    }
                } else {
                    System.out.println("No hay recibos");
                }
                return;
            } else {
                remps.skipBytes(24);
            }
        }
        System.out.println("Empleado no encontrado");
    }
}