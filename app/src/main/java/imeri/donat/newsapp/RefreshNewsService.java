package imeri.donat.newsapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RefreshNewsService extends Service {
    private Thread t;

    public RefreshNewsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int refreshRate=intent.getIntExtra("refresh_rate",60);
        t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(refreshRate*60*1000);
                    Intent finished=new Intent();
                    finished.setAction("refreshNews");
                    sendBroadcast(finished);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            t.interrupt();
        }
        catch (Exception e){

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
