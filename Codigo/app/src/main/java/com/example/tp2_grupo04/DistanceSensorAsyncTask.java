package com.example.tp2_grupo04;

import android.os.AsyncTask;

public class DistanceSensorAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private long initialTime;
    private MenuActivity menuActivity;

    public DistanceSensorAsyncTask(MenuActivity menuActivity) {
        this.menuActivity = menuActivity;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        this.initialTime = System.currentTimeMillis();
        long actualTime;
        while ((actualTime = (System.currentTimeMillis() - initialTime)) <= 1000 && menuActivity.isCloseDistance()) {
        }
        if (actualTime > 1000) {
            //salio del while porque pasó 1 segundo
            //entonces el usuario no hizo el gesto
            return false;
        } else {
            //salio del while porque el sensor no detecta nada cerca
            // entonces el usuario hizo el gesto
            return true;
        }

    }

    /*
        En caso de que el tiempo sea menor a 1 segundo, se considera como gesto.
        Por lo tanto, se lanza la activity Diagnosis.
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            this.menuActivity.lanzarActivityDiagnosis();
        }
    }


}