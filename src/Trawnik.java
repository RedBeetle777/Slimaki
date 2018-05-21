import java.util.Random;

class Trawnik
{
    private int rozmiar;
    private int wysokosc_trawy;
    private int[] trawa;
    private int ready;
    Trawnik(int s, int h)
    {
        rozmiar = s;
        wysokosc_trawy = h;
        trawa = new int[rozmiar];
    }

    synchronized void init()
    {
        for(int i=0; i<rozmiar; i++)
        {
            Random rand = new Random();
            int h = rand.nextInt(wysokosc_trawy);
            trawa[i] = h;
        }
        ready = 1;
        notifyAll();
    }

    synchronized void incr() throws InterruptedException
    {
        while(ready == 0)
            wait();
        try
        {
            Random rand = new Random();
            int i = rand.nextInt(wysokosc_trawy);
            if(trawa[i] < wysokosc_trawy)
                trawa[i]++;
        }
        catch (Exception e)
        {
            System.out.println("Thread  interrupted.");
        }
    }
    synchronized void decr() throws InterruptedException
    {
        while(ready == 0)
            wait();
        try
        {
            Random rand = new Random();
            int i = rand.nextInt(wysokosc_trawy);
            if(trawa[i] > 0)
                trawa[i]--;
        }
        catch (Exception e)
        {
            System.out.println("Thread  interrupted.");
        }
    }
    synchronized void stan() throws InterruptedException
    {
        while(ready == 0)
            wait();
        try
        {
            System.out.println("Stan trawnika");
            for(int i=0; i<rozmiar; i++)
                System.out.println(i + " -> " + trawa[i]);
        }
        catch (Exception e)
        {
            System.out.println("Thread  interrupted.");
        }
    }
    void end()
    {
        for(int i=0; i<rozmiar; i++)
            System.out.println(i + " -> " + trawa[i]);
    }
}

class Slimak implements Runnable
{
    private Trawnik trawnik;

    Slimak(Trawnik l)
    {
        trawnik = l;
    }
    public void run()
    {
        System.out.println("Running " + Thread.currentThread().getName());
        try
        {
            String name = Thread.currentThread().getName();
            if(name == "init")
                trawnik.init();

            while(true)
            {
                if(name == "slimak")
                    trawnik.decr();
                if(name == "trawnik")
                    trawnik.incr();
                if(name == "stan")
                    trawnik.stan();
                Thread.sleep(500);
            }
        }
        catch (Exception e)
        {
            System.out.println("Interrupted");
        }
    }
}

class Test
{
    public static void main(String args[])
    {
        Trawnik l  = new Trawnik(10, 10);
        int n = 5;

        Thread init = new Thread(new Slimak(l), "init");
        Thread status = new Thread(new Slimak(l), "stan");
        Thread lawn = new Thread(new Slimak(l), "trawnik");
        Thread slimes[] = new Thread[n];

        for(int i=0; i<n; i++)
            slimes[i] = new Thread(new Slimak(l), "slimak");

        init.start();
        status.start();
        lawn.start();
        for(int i=0; i<n; i++)
            slimes[i].start();

        try
        {
            init.join();
            status.join();
            lawn.join();
            for(int i=0; i<n; i++)
                slimes[i].join();
        }
        catch (Exception e)
        {
            System.out.println("Interrupted");
        }
        l.end();
    }
}
