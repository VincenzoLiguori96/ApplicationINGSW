package com.example.applicationingsw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationingsw.model.Category;
import com.example.applicationingsw.model.Item;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ApplyFilterActivity extends Activity {
    private RangeSeekBar priceRange ;
    private LinearLayout tagContainer ;
    private Spinner spinner ;
    private TextInputEditText manufacturerTextInput;
    private TextInputEditText keywordTextInput;
    private TextView doneTextView;
    private List<String> tagsList = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_filters);
        setLayout();
        spinner = findViewById(R.id.spinnerCategories);
        tagContainer = findViewById(R.id.tagsContainer);
        priceRange = findViewById(R.id.priceRange);
        manufacturerTextInput = findViewById(R.id.manufacturerTextInput);
        manufacturerTextInput.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        keywordTextInput = findViewById(R.id.insertKeywordTextInput);
        keywordTextInput.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        keywordTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(textView.getText()!= null){
                    createTagButton(" "+textView.getText().toString());
                    tagsList.add(textView.getText().toString());
                    textView.setText("");
                }
                return false;
            }
        });
        doneTextView = findViewById(R.id.alertDone);
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ApplyFilterActivity.this,getQueryStringParameters(), Toast.LENGTH_SHORT).show();
            }
        });
        priceRange.getRightSeekBar().setIndicatorTextDecimalFormat("0.00");
        priceRange.getLeftSeekBar().setIndicatorTextDecimalFormat("0.00");
        priceRange.setProgress(999.99f);
        priceRange.setProgress(0,999.99f);
        getCategoriesFromAPI();
    }



    private void setLayout(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getWindow().setLayout((int)Math.round(width *.8),(int)Math.round(height *.65));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 80;
        getWindow().setAttributes(params);
    }

    public boolean anyFilterSelected(){
        if(manufacturerTextInput.getText() != null && !manufacturerTextInput.getText().equals("")){
            return true;
        }
        if(spinner.getSelectedItem().toString() != null && !spinner.getSelectedItem().toString().equals("") && !spinner.getSelectedItem().toString().equals("Select an item...")){
            return true;
        }
        if(priceRange.getLeftSeekBar().getProgress() != 0){
            return true;
        }
        if(priceRange.getRightSeekBar().getProgress()!= 999.99){
            return true;
        }
        if(!tagsList.isEmpty()){
            return true;
        }
        return false;
    }

    public float getPriceMin(){
        if(priceRange.getLeftSeekBar().getProgress() < priceRange.getRightSeekBar().getProgress()){
            return priceRange.getLeftSeekBar().getProgress();
        }
        else{
            return priceRange.getRightSeekBar().getProgress();
        }
    }

    public float getPriceMax(){
        if(priceRange.getLeftSeekBar().getProgress() > priceRange.getRightSeekBar().getProgress()){
            return priceRange.getLeftSeekBar().getProgress();
        }
        else{
            return priceRange.getRightSeekBar().getProgress();
        }
    }

    public boolean priceFilterSelected(){
        if(priceRange.getLeftSeekBar().getProgress() != 0){
            return true;
        }
        if(priceRange.getRightSeekBar().getProgress()!= 999.99){
            return true;
        }
        else{
            if(priceRange.getRightSeekBar().getProgress()!= 0 && priceRange.getLeftSeekBar().getProgress()!=999.99){
                return true;
            }
            return false;
        }
    }

    public boolean isExactPrice(){
        return priceRange.getLeftSeekBar().getProgress() == priceRange.getRightSeekBar().getProgress();
    }

    public String getQueryStringParameters(){
        String queryString = "?";
        String price="";
        String priceMin="";
        String priceMax="";
        String manufacturer="";
        String category="";
        String tags="";
        if(anyFilterSelected()){
            if (priceFilterSelected()){
                if(isExactPrice()){
                     price = "price="+priceRange.getLeftSeekBar().getProgress();
                }
                else{
                     priceMin = "priceMin="+getPriceMin();
                     priceMax = "priceMax="+getPriceMax();
                }
                if(!manufacturerTextInput.getText().equals("") && manufacturerTextInput != null){
                     manufacturer = "manufacturer=" + manufacturerTextInput.getText();
                }
                if(spinner.getSelectedItem().toString() != null && !spinner.getSelectedItem().toString().equals("") && !spinner.getSelectedItem().toString().equals("Select an item...")){
                     category = "category="+ spinner.getSelectedItem().toString();
                }
                tags ="tags=";
                if(!tagsList.isEmpty()){
                    for(String tag : tagsList){
                        tags +="+";
                        tags+=tag;
                    }
                }
            }
        }
        queryString = category;
        if(price!= ""){
            queryString +="&"+price;
        }
        else{
            queryString += "&"+priceMax+"&"+priceMin+"&"+manufacturer+"&"+tags;
        }
        return queryString;
    }

    private void createTagButton(String tagName){
        Button buyButton = new Button(this);
        buyButton.setText(tagName);
        Drawable img = getDrawable(R.drawable.tag_button_icon);
        buyButton.setBackgroundColor(Color.WHITE);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(Color.WHITE);
        shape.setStroke(2,Color.parseColor("#ede9e8") );
        shape.setCornerRadius(15);
        buyButton.setBackground(shape);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(18, 0, 18, 0);
        buyButton.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        buyButton.getCompoundDrawables()[0].setBounds(10,0,16,16);
        tagContainer.addView(buyButton,layoutParams);
        tagContainer.refreshDrawableState();
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagButtonClicked(view);
            }
        });
    }


    private void tagButtonClicked(View buttonView){
        Button button = (Button) buttonView;
        tagsList.remove(button.getText());
        tagContainer.removeView(buttonView);
    }

    public void getCategoriesFromAPI(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiItemEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/categories/allcategories";
        final JsonObjectRequest request = new JsonObjectRequest(apiItemEndpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArrayOfCategories = response.getJSONArray("elem");
                    for(int i = 0; i< jsonArrayOfCategories.length();i++){
                        JSONObject item = jsonArrayOfCategories.getJSONObject(i);
                        String name = item.getString("name");
                        categories.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    requestCompleted();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    public void requestCompleted(){
        categories.add(0,"Select an item...");
        categories.add("");
        setSpinnerAdapter(categories);
    }

    private void setSpinnerAdapter(final List<String> categories){
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item_layout,categories){
            @Override
            public boolean isEnabled(int position){
                if(position == 0 || position == categories.size())
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
