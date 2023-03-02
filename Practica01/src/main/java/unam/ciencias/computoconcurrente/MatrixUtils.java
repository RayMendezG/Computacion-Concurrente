package unam.ciencias.computoconcurrente;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MatrixUtils implements Runnable{
    private int threads;
    private static int[] posiblesMinimos; // Arreglo para que cada hilo guarde su minimo encontrado
    private static int[] posiblesPromedios; //Arreglo para que cada hilo guarde la suma de su subarreglo
    private static int[] matrixGlobal; 
    private static int secciones; 


    public MatrixUtils() {
        this.threads = 1;
    }

    public MatrixUtils(int threads) {
        this.threads = threads;
    }

    @Override
    public void run() {
        int promedio_hilo = 0;
        String nombre_hilo = Thread.currentThread().getName();
        int inicio = Integer.valueOf(nombre_hilo);

        //Verificamos que no pasemos el limite del arreglo
        if ((inicio + secciones) > matrixGlobal.length-1) {
            for (int i = inicio; i < matrixGlobal.length; i++) {
                promedio_hilo = promedio_hilo +  matrixGlobal[i];
            }
            //insertamos en el arreglo posiblesPromedios a la suma de los elementos del subarreglo
            insertar(promedio_hilo);
        }else{
            for (int i = inicio; i < inicio + secciones; i++) {
                promedio_hilo = promedio_hilo +  matrixGlobal[i];
            }
            insertar(promedio_hilo);
        }
        
    }

    public double findAverage(int[][] matrix) throws InterruptedException{
        posiblesPromedios = new int[threads];
        secciones = (matrix.length * matrix[0].length) / this.threads;
        matrixGlobal = new int[matrix.length * matrix[0].length];
        int inicio = 0;

        //Caso base cuando hay 1 thread, no hay trabajo que dividir
        if (this.threads == 1) {
            double promedioTodos = 0.0;
            promedioTodos = Average(matrix);
            BigDecimal bd = new BigDecimal(promedioTodos).setScale(2, RoundingMode.HALF_DOWN); 
            promedioTodos = bd.doubleValue(); //Redondeamos a dos decimales para pasar los Test
            
            return promedioTodos;
        }

        int index = 0;

        //Transformamos la matriz en un arreglo de 1 dimension
        while (index < matrixGlobal.length) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    matrixGlobal[index] = matrix[i][j];
                    index = index + 1;
                }
            }
        }

        //Creamos los hilos y los inicializamos
        for (int i = 0; i < this.threads; i++) {
            Thread t = new Thread(new MatrixUtils());
            t.setName(String.valueOf(inicio));
            t.start();
            t.join();
            inicio = inicio + secciones;
        }

        //De manera secuencial calculamos el promedio de los resultados de la suma de los sub-arreglos
        double promedioTodos = 0;
        for (int i = 0; i < posiblesPromedios.length; i++) {
            promedioTodos = promedioTodos + posiblesPromedios[i];
        }

        promedioTodos = promedioTodos / matrixGlobal.length;

        if (promedioTodos <= 1) {
            return Math.ceil(promedioTodos);
            
        }
        //Redondeamos a 2 decimales
        BigDecimal bd = new BigDecimal(promedioTodos).setScale(2, RoundingMode.HALF_DOWN);
        promedioTodos = bd.doubleValue();
        return promedioTodos;
    }

    /**
     * Metodo que inserta un entero en el arreglo posiblesPromedios
     * en la primera posición vacia.
     * @param elem - numero a insertar
     * @return void 
     */

    public void insertar(int elem){
        for(int i=0; i < posiblesPromedios.length; i++){
            if(posiblesPromedios[i] == 0) {
                posiblesPromedios[i] = elem;
                break;
            }    

        }
        
    }

    /**
     * Metodo que recorre una matriz de dos dimensiones 
     * @param matrix - matriz de dos dimensiones 
     * @return promedio - promedio de la matriz
     */
    public double Average(int[][] matrix){
    double promedio = 0.0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                promedio = promedio + matrix[i][j];
            }
        }
        
        return promedio / (matrix.length * matrix[0].length);
    } 

}
