package unam.ciencias.computoconcurrente;
import java.lang.Math;
public class PrimeNumberCalculator implements Runnable{

    private int threads;
    private static int numPrimo;
    public static boolean result;
    public static int longitudSubInter; //Dividimos el intervalo [2,N-1] en this.threads cantidad de sub interbalos, uno por cada hilo



    public PrimeNumberCalculator() {
        this.threads = 1;
    }

    public PrimeNumberCalculator(int threads) {
        this.threads = threads > 1 ? threads : 1;
    }
    

    public boolean isPrime(int n) throws InterruptedException{
        longitudSubInter = Math.round(Math.round(Math.sqrt(n)+1)  / this.threads); //Dividimos hasta la raiz de n+1
        int inicio = 1;
        result = true;
        numPrimo = n;

        //caso base de 1 y 0
        if(n == 0 || n == 1){
            result = false;
            return result;
        }

        //Creamos los threads y los inicializamos
        for (int i = 0; i < this.threads; i++) {
            Thread t = new Thread(new PrimeNumberCalculator());
            t.setName(String.valueOf(inicio));
            t.start();
            t.join();
            inicio = inicio + longitudSubInter;
        }
        
        return result;

    }
    

    @Override
    public void run(){
        String nombre_hilo = Thread.currentThread().getName();
        int inicio = Integer.valueOf(nombre_hilo);

        //Revisamos que el hilo no verifique mas alla de la raiz + 1 de numero
        if ((inicio + longitudSubInter) > Math.round(Math.sqrt(numPrimo+1)) ) {
            for (int i = inicio; i < numPrimo; i++) {
                if (numPrimo % i == 0 && i != 1) {
                    result = false;
                    return;
                }
            }
        }else{
            for (int i = inicio; i < inicio+longitudSubInter; i++) {
                if (numPrimo % i == 0 && i != 1) {
                    result = false;
                    return;
                }
            }
            
        }
        
    }

    
}
