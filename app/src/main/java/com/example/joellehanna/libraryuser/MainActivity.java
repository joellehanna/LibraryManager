package com.example.joellehanna.libraryuser;

//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;

//public class MainActivity extends AppCompatActivity {
//
//    private Button libraryButton;
//    private Button bookScan;
//    private TextView message;
//
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////
////        message = findViewById(R.id.welcomeMsg);
////        message.setText(R.string.welcome);
////
////        libraryButton = findViewById(R.id.searchBooks);
////        libraryButton.setText(R.string.search_books);
////
////        bookScan = findViewById(R.id.scanBook);
////        bookScan.setText(R.string.scan_book);
////    }
////
////    public void clickedSearchBook(View view) {
////
////        Intent intent = new Intent(this, searchActivity.class);
////        startActivity(intent);
////
////    }
////
////    public void clickedScanBook(View view) {
////
////        Intent intent = new Intent(this, LibraryActivity.class);
////        startActivity(intent);
////
////    }
//
//
//
//}


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

    public class MainActivity extends AppCompatActivity {

        private Button button;
        private TextView contentTxt;
        private Button libraryButton;
        private Button bookScan;
        private TextView message;

        public MainActivity() {
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            message = findViewById(R.id.welcomeMsg);
            message.setText(R.string.welcome);

            libraryButton = findViewById(R.id.searchBooks);
            libraryButton.setText(R.string.search_books);

            bookScan = findViewById(R.id.scanBook);
            bookScan.setText(R.string.scan_book);

            button = (Button) this.findViewById(R.id.scanBook);
            contentTxt = (TextView)findViewById(R.id.scanText);
            final Activity activity = this;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setPrompt("Scan");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();
                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    Log.d("MainActivity", "Cancelled scan");
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    contentTxt.setText("CONTENT: " + result.getContents());
                }

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }



        }

        public void clickedSearchBook(View view) {

            Intent intent = new Intent(this, searchActivity.class);
            startActivity(intent);

        }

        public void clickedScanBook(View view) {

            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);

        }

    }