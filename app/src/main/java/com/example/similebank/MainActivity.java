/**
 * Questa applicazione è compatibile da Android 8(Oreo) in su
 * Si tratta di un libretto risparmio che tiene conto delle diverse operazioni possibili tra cui anche il salvataggio
 */

package com.example.similebank;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    TextView SchermoSaldo, prendiSaldo, prendiDeposit, prendiPreleva, cronologia, interest, logs;
    ImageButton b;
    BankAccount bank;
    double saldo;
    LocalDateTime time;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm:ss");
    DecimalFormat frmt = new DecimalFormat();
    ArrayList<String> arr = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SchermoSaldo = findViewById(R.id.saldo);
        prendiSaldo = (EditText) findViewById(R.id.getSaldo);
        prendiDeposit = (EditText) findViewById(R.id.depositAmount);
        prendiPreleva = (EditText) findViewById(R.id.prelevaAmount);
        interest = (EditText) findViewById(R.id.getInteress);
        b = findViewById(R.id.crea);
        cronologia = findViewById(R.id.CronologiaOp);
        logs = findViewById(R.id.cronLog);

    }

    public void createBank(View v){

        String s = prendiSaldo.getText().toString();

        if (s.isEmpty())
            saldo = 0;
        else
            saldo = Double.parseDouble(s);

        bank = new BankAccount(saldo);
        SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));
        prendiSaldo.setText("Non più utilizzabile");
        prendiSaldo.setEnabled(false);
        b.setEnabled(false);
        SchermoSaldo.setBackgroundColor(Color.GREEN);


        time = LocalDateTime.now();
        cronologia.append("/>.Conto creato con saldo: € " + frmt.format(bank.getBalance()) + "\n" + time.format(formatter) + "\n");
        arr.add("#Conto creato con saldo: € " + frmt.format(bank.getBalance()) + "\n" + time.format(formatter) + "\n");
    }

    public void deposit(View v){
        String s = prendiDeposit.getText().toString();
        if (s.isEmpty())
            saldo = 0;
        else
            saldo = Double.parseDouble(s);
        bank.deposit(saldo);
        SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));
        SchermoSaldo.setBackgroundColor(Color.GREEN);
        prendiDeposit.setText("");
        time = LocalDateTime.now();
        cronologia.append("/>.Deposito di: € " + saldo + ", saldo attuale: € " + frmt.format(bank.getBalance()) + "\n" +
                time.format(formatter) + "\n");

        /*storico*/
        String t = "#Deposito di: € " + saldo + " [saldo attuale:" + bank.getBalance() + "] " + time.format(formatter) + "\n";
        arr.add(t);
    }

    public void preleva(View v){
        String s = prendiPreleva.getText().toString();
        if (s.isEmpty())
            saldo = 0;
        else
            saldo = Double.parseDouble(s);

        if (saldo <= bank.getBalance() && bank.getBalance() > 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Avviso Saldo");
            alert.setMessage("Hai prelevato € " + saldo);
            alert.setNeutralButton("OK!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Prelievo effettuato", Toast.LENGTH_LONG).show();
                }
            });
            alert.show();

            bank.withdrow(saldo);
            SchermoSaldo.setBackgroundColor(Color.RED);
            SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));

            prendiPreleva.setText("");
            time = LocalDateTime.now();
            cronologia.append("/>.Prelievo di: € " + saldo + " [saldo attuale:" + bank.getBalance() + "] "
                    + frmt.format(bank.getBalance()) + "\n" + time.format(formatter) + "\n");
            /*storico*/
            String t = "#Prelievo di: € " + saldo + time.format(formatter) + "\n";
            arr.add(t);
            return;
        }

        if (bank.getBalance() < saldo && bank.getBalance() > 0) {
            AlertDialog.Builder zero = new AlertDialog.Builder(this);
            zero.setTitle("Errore Saldo");
            zero.setMessage("Saldo Non Sufficeinte");
            zero.setNeutralButton("OK!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Prelievo Annullato", Toast.LENGTH_LONG).show();
                }
            });
            zero.show();

            prendiPreleva.setText("");
            time = LocalDateTime.now();
            cronologia.append("/>.Prelievo Annullato, Saldo insufficiente " + "\n" + time.format(formatter) + "\n");
            return;
        }

        double balance = bank.getBalance();
        if (balance == 0) {
            AlertDialog.Builder zero = new AlertDialog.Builder(this);
            zero.setTitle("Conto con saldo zero");
            zero.setMessage(" Il tuo conto ha raggiunto saldo zero,\n impossibile prelvare");
            zero.setNeutralButton("OK!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Prelievo Annullato", Toast.LENGTH_LONG).show();
                }
            });
            zero.show();

            bank.withdrow(saldo);
            SchermoSaldo.setBackgroundColor(Color.RED);
            SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));

            time = LocalDateTime.now();
            cronologia.append("/>.Prelievo annullato saldo 0\n" + time.format(formatter) + "\n");
            prendiPreleva.setText("");
            return;
        }

    }

    public void pulisciLista(View v){
        cronologia.setText("");

    }

    public void pulisciLog(View v){
        logs.setText("");

    }

    public void all(View v){
        if (bank != null && bank.getBalance() > 0) {
            saldo = bank.getBalance();

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Avviso Prelievo Totale");
            alert.setMessage("Hai prelevato tutto ");
            alert.setNeutralButton("OK!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Prelievo effettuato", Toast.LENGTH_LONG).show();
                }
            });
            alert.show();
            bank.withdrow(saldo);
            SchermoSaldo.setBackgroundColor(Color.RED);
            SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));
            time = LocalDateTime.now();
            cronologia.append("/>.Hai prelevato tutto (€ " + saldo + ") \n" + time.format(formatter) + "\n");

            /*storico*/
            String t = "#Hai prelevato tutto (€ " + saldo + ")" + time.format(formatter) + "\n";
            arr.add(t);
        } else
            return;
    }

    public void info(View v) {

        AlertDialog.Builder infoApp = new AlertDialog.Builder(this);
        infoApp.setTitle("ISTRUZIONI");
        infoApp.setMessage("Cliccando su NEW " +
                "crei il tuo conto con saldo 0, altrimenti inserisci un importo e poi clicca " +
                "NEW");

        infoApp.setNeutralButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        infoApp.show();

    }

    public void addI(View v){
        if (bank != null && bank.getBalance() > 0) {

            String s = interest.getText().toString();
            if (s.isEmpty())
                saldo = 0.004;
            else
                saldo = Double.parseDouble(s);

            bank.addInterest(saldo);
            SchermoSaldo.setBackgroundColor(Color.GREEN);
            SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));
            interest.setText("");
            time = LocalDateTime.now();
            cronologia.append("/>.Interessi aggiunti: (" + saldo + "); " +
                    "Saldo = " + bank.getBalance() + "\n" + time.format(formatter) + "\n");

            /*Storico*/
            arr.add("#Interessi aggiunti: (" + saldo + ") " + "\n" + time.format(formatter) + "\n");

        } else if (bank != null && bank.getBalance() == 0) {
            interest.setText("");
            time = LocalDateTime.now();
            cronologia.append("/>.Impossibile aggiungere interessi ad un conto con saldo 0\n" + time.format(formatter) + "\n");
            return;
        } else {
            return;
        }
    }

    public void salvataggio(View v) throws IOException {

        if (bank == null) {
            time = LocalDateTime.now();
            cronologia.append("/>.Conto non ancora istanziato\n");
            return;
        }
        OutputStreamWriter osw = new OutputStreamWriter(this.openFileOutput("save.txt", Context.MODE_PRIVATE));
        osw.write("" + bank.getBalance());
        osw.close();
        time = LocalDateTime.now();
        cronologia.append("/>.Hai salvato il saldo\n" + time.format(formatter) + "\n");


        ObjectOutputStream stream = new ObjectOutputStream(this.openFileOutput("log.txt", Context.MODE_PRIVATE));
        stream.writeObject(arr);
        stream.flush();
        stream.close();

        time = LocalDateTime.now();
        cronologia.append("/>.Hai salvato i movimenti\n" + time.format(formatter) + "\n");

    }

    public void caricamento(View v) throws IOException, ClassNotFoundException {
        BufferedReader in;

        if (bank != null) {
            time = LocalDateTime.now();
            cronologia.append("/>.Conto gia caricato" + time.format(formatter) + "\n");
            return;
        }

        InputStream is = openFileInput("save.txt");
        InputStreamReader isr = new InputStreamReader(is);
        in = new BufferedReader(isr);
        String t = in.readLine();


        if (t.isEmpty()) {

            bank = new BankAccount(0);
            SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));
            prendiSaldo.setText("Non più utilizzabile");
            prendiSaldo.setEnabled(false);
            b.setEnabled(false);
            SchermoSaldo.setBackgroundColor(Color.GREEN);

            time = LocalDateTime.now();
            cronologia.append("/>.Conto non caricato e creato con saldo: € " + frmt.format(bank.getBalance()) + "\n"
                    + time.format(formatter) + "\n");

        }

        saldo = Double.parseDouble(t);
        bank = new BankAccount(saldo);
        SchermoSaldo.setText("Saldo: € " + frmt.format(bank.getBalance()));
        prendiSaldo.setText("Non più utilizzabile");
        prendiSaldo.setEnabled(false);
        b.setEnabled(false);
        SchermoSaldo.setBackgroundColor(Color.GREEN);

        ObjectInputStream stream = new ObjectInputStream(openFileInput("log.txt"));
        arr = (ArrayList<String>) stream.readObject();
        stream.close();

        isr.close();
        is.close();
        time = LocalDateTime.now();
        cronologia.append("/>.Conto caricato e creato con saldo: € " + frmt.format(bank.getBalance()) + "\n"
                + time.format(formatter) + "\n");
        cronologia.append("Caricata lista movimenti \n"
                + time.format(formatter) + "\n");

    }

    public void log(View v) throws IOException, ClassNotFoundException {

        pulisciLog(logs);

        ObjectInputStream stream = new ObjectInputStream(openFileInput("log.txt"));
        ArrayList<String> l = (ArrayList<String>) stream.readObject();
        stream.close();

        for (int x = 0; x < l.size(); x++) {
            String t = l.get(x);
            logs.append(t);

        }

    }

}
