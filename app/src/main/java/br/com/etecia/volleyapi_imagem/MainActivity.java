import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ImageViewAndroidActivity extends ListActivity {
    private static final String[] OPCOES = {"Carregar Imagens",
            "Exibir Imagens"  };

    private static final String[] ACOES = {"TELA_IMAGEM",
            "TELA_EXI"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, OPCOES);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent it = new Intent(ACOES[position]);
        startActivity(it);
    }
}

import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.ImageView;

public class CarregarImagemUrl extends Activity {

    private EditText edtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tela_url_img);
        edtUrl = (EditText)findViewById(R.id.editText1);
        // Obtendo a ultima URL digitada
        SharedPreferences preferencias = getSharedPreferences(
                "configuracao", MODE_PRIVATE);
        String url = preferencias.getString("url_imagem", "http://");
        edtUrl.setText(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Salva a URL para utiliza-la quando essa tela for re-aberta
        SharedPreferences preferencias = getSharedPreferences(
                "configuracao", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("url_imagem", edtUrl.getText().toString());
        editor.commit();
    }

    public void baixarImagemClick(View v){
        new DownloadImagemAsyncTask().execute(
                edtUrl.getText().toString());
    }


    class DownloadImagemAsyncTask extends
            AsyncTask<String, Void, Bitmap>{

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(
                    CarregarImagemUrl.this,
                    "Aguarde", "Carregando a  imagem...");
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String urlString = params[0];

            try {
                URL url = new URL(urlString);
                HttpURLConnection conexao = (HttpURLConnection)
                        url.openConnection();
                conexao.setRequestMethod("GET");
                conexao.setDoInput(true);
                conexao.connect();

                InputStream is = conexao.getInputStream();
                Bitmap imagem = BitmapFactory.decodeStream(is);
                return imagem;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null){
                ImageView img = (ImageView)findViewById(R.id.imageView1);
                img.setImageBitmap(result);
            } else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(CarregarImagemUrl.this).
                                setTitle("Erro").
                                setMessage("Não foi possivel carregar imagem, tente
                                        novamente mais tarde!").
                setPositiveButton("OK", null);
                builder.create().show();
            }
        }
    }
}