package com.team4.caratan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellCar_InfoFragment extends Fragment {

    private Button btnNext;

    private EditText edtYear, edtMileage, edtPrice, edtDesc;
    private Spinner spinMake, spinModel, spinType, spinColor;

    private ArrayList<String> makeList = new ArrayList<>();
    private ArrayList<String> modelList = new ArrayList<>();
    private ArrayList<String> typeList = new ArrayList<>();
    private ArrayList<String> colorList = new ArrayList<>();

    private ArrayAdapter<String> makeAdapter;
    private ArrayAdapter<String> modelAdapter;
    private ArrayAdapter<String> typeAdapter;
    private ArrayAdapter<String> colorAdapter;

    private String selectedMake = null, selectedModel = null, selectedType = null, selectedColor = null,
            year, mileage, price, desc;

    public SellCar_InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sell_car__info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtYear = requireView().findViewById(R.id.SELL_edtYear);
        edtMileage = requireView().findViewById(R.id.SELL_edtMileage);
        edtPrice = requireView().findViewById(R.id.SELL_edtPrice);
        edtDesc = requireView().findViewById(R.id.SELL_edtDesc);

        spinMake = requireView().findViewById(R.id.SELL_spinMake);
        spinModel = requireView().findViewById(R.id.SELL_spinModel);
        spinType = requireView().findViewById(R.id.SELL_spinType);
        spinColor = requireView().findViewById(R.id.SELL_spinColor);

        getMakeSpinnerData();
        spinMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getId() == R.id.SELL_spinMake) {
                    selectedMake = adapterView.getSelectedItem().toString();
                    getModelSpinnerData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getId() == R.id.SELL_spinModel) {
                    selectedModel = adapterView.getSelectedItem().toString();
                    getTypeSpinnerData();
                    getColorSpinnerData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getId() == R.id.SELL_spinType) {
                    selectedType = adapterView.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getId() == R.id.SELL_spinColor) {
                    selectedColor = adapterView.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnNext = requireView().findViewById(R.id.SELL_btnNext1);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                year = edtYear.getText().toString().trim();
                mileage = edtMileage.getText().toString().trim();
                price = edtPrice.getText().toString().trim();
                desc = edtDesc.getText().toString().trim();

                if(year.isEmpty()) {
                    edtYear.setError("Tahun Mobil Harus Diisi!");
                    edtYear.requestFocus();
                    return;
                }
                if(Double.parseDouble(year) > 2024 || Double.parseDouble(year) < 1901) {
                    edtYear.setError("Tahun Mobil Tidak tepat!");
                    edtYear.requestFocus();
                    return;
                }
                if(mileage.isEmpty()) {
                    edtMileage.setError("Kilometer Mobil Harus Diisi!");
                    edtMileage.requestFocus();
                    return;
                }
                if(Double.parseDouble(mileage) < 11 || Double.parseDouble(mileage) > 300000) {
                    edtMileage.setError("Kilometer Mobil Tidak sesuai!");
                    edtMileage.requestFocus();
                    return;
                }
                if(price.isEmpty()) {
                    edtPrice.setError("Harga Mobil Harus Diisi!");
                    edtPrice.requestFocus();
                    return;
                }
                if(Double.parseDouble(price) < 2000000) {
                    edtPrice.setError("Tidak dapat menjual mobil dibawah Rp 2.000.000!");
                    edtMileage.requestFocus();
                    return;
                }
                if(desc.isEmpty()) {
                    edtDesc.setError("Deskripsi Mobil Harus Diisi!");
                    edtDesc.requestFocus();
                    return;
                }

                ((SellCarActivity)getActivity()).set_info(selectedMake, selectedModel, selectedType, selectedColor,
                                                            year, mileage, price, desc);

                ((SellCarActivity)getActivity()).add_progress(33);
                ((SellCarActivity)getActivity()).openSellCar_Photo();
            }
        });
    }

    private void getMakeSpinnerData() {
        //progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constant.URL_GETMAKE_LIST,
                response -> {

                    //carMakeArrayList.clear();
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray jsonArray = obj.getJSONArray("carmake");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String make_name = object.getString("make_name");

                            makeList.add(make_name);
                            makeAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, makeList);
                            makeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinMake.setAdapter(makeAdapter);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //progressBar.setVisibility(View.GONE);
                },
                error -> {
                    Toast.makeText(requireContext(), "Silahkan cek kembali internet anda!",
                            Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.GONE);
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    private void getModelSpinnerData() {
        final String makeName = selectedMake;
        //progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constant.URL_GETMODEL_LIST_BYMAKE,
                response -> {

                    //carMakeArrayList.clear();
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray jsonArray = obj.getJSONArray("carmodel");
                        modelList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String model_name = object.getString("model_name");
                            modelList.add(model_name);
                            modelAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, modelList);
                            modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinModel.setAdapter(modelAdapter);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //progressBar.setVisibility(View.GONE);
                },
                error -> {
                    Toast.makeText(requireContext(), "Silahkan cek kembali internet anda!",
                            Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.GONE);
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("make_name", makeName);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    private void getTypeSpinnerData() {
        final String modelName = selectedModel;

        //progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constant.URL_GETTYPE_LIST_BYMODEL,
                response -> {

                    //carMakeArrayList.clear();
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray jsonArray = obj.getJSONArray("cartype");
                        typeList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String model_type = object.getString("model_type");
                            typeList.add(model_type);
                            typeAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, typeList);
                            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinType.setAdapter(typeAdapter);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //progressBar.setVisibility(View.GONE);
                },
                error -> {
                    Toast.makeText(requireContext(), "Silahkan cek kembali internet anda!",
                            Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.GONE);
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("model_type", modelName);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    private void getColorSpinnerData() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Constant.URL_GETCOLOR_LIST,
                response -> {

                    //carMakeArrayList.clear();
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray jsonArray = obj.getJSONArray("carcolor");
                        colorList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String model_type = object.getString("color");
                            colorList.add(model_type);
                            colorAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, colorList);
                            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinColor.setAdapter(colorAdapter);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //progressBar.setVisibility(View.GONE);
                },
                error -> {
                    Toast.makeText(requireContext(), "Silahkan cek kembali internet anda!",
                            Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.GONE);
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }
}