package com.team4.caratan;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SellCar_CoFragment extends Fragment {

    private mDBhandler dbHandler;

    private String make, model, type, color, year, mileage, price, desc;
    private TextView txtMake, txtModel, txtType, txtColor, txtYear, txtMileage, txtPrice, txtDesc;

    private Bitmap bp1, bp2, bp3;
    private ImageView photo1, photo2, photo3;

    private Button Checkout;

    public SellCar_CoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        make = ((SellCarActivity)getActivity()).getSelectedMake();
        model = ((SellCarActivity)getActivity()).getSelectedModel();
        type = ((SellCarActivity)getActivity()).getSelectedType();
        color = ((SellCarActivity)getActivity()).getSelectedColor();
        year = ((SellCarActivity)getActivity()).getYear();
        mileage = ((SellCarActivity)getActivity()).getMileage();
        price = ((SellCarActivity)getActivity()).getPrice();
        desc = ((SellCarActivity)getActivity()).getDesc();

        bp1 = ((SellCarActivity)getActivity()).getPhoto1();
        bp2 = ((SellCarActivity)getActivity()).getPhoto2();
        bp3 = ((SellCarActivity)getActivity()).getPhoto3();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sell_car__co, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photo1 = requireView().findViewById(R.id.sell_carPhoto4);
        photo2 = requireView().findViewById(R.id.sell_carPhoto5);
        photo3 = requireView().findViewById(R.id.sell_carPhoto6);

        txtMake = requireView().findViewById(R.id.SELL_Final_txtMerek);
        txtModel = requireView().findViewById(R.id.SELL_Final_txtModel);
        txtType = requireView().findViewById(R.id.SELL_Final_txtTipe);
        txtColor = requireView().findViewById(R.id.SELL_Final_txtWarna);
        txtYear = requireView().findViewById(R.id.SELL_Final_txtTahun);
        txtMileage = requireView().findViewById(R.id.SELL_Final_txtKM);
        txtPrice = requireView().findViewById(R.id.SELL_Final_txtHarga);
        txtDesc = requireView().findViewById(R.id.SELL_Final_txtDesc);

        Checkout = requireView().findViewById(R.id.SELL_btnBayar);

        DecimalFormat decim = new DecimalFormat("###,###");
        double km = Double.parseDouble(mileage);
        double rp = Double.parseDouble(price);
        String decim_km = decim.format(km);
        String decim_rp = decim.format(rp);

        txtMake.setText(make);
        txtModel.setText(model);
        txtType.setText(type);
        txtColor.setText(color);
        txtYear.setText(year);
        txtMileage.setText(decim_km + " KM");
        txtPrice.setText("Rp " + decim_rp);
        txtDesc.setText(desc);

        requireActivity().runOnUiThread(() -> photo1.setImageBitmap(bp1));
        requireActivity().runOnUiThread(() -> photo2.setImageBitmap(bp2));
        requireActivity().runOnUiThread(() -> photo3.setImageBitmap(bp3));

        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadCar();
            }
        });
    }

    private void uploadCar() {

        Toast.makeText(requireContext(), "Upload Car",
                Toast.LENGTH_SHORT).show();

        dbHandler = new mDBhandler(requireContext());

        try {
            dbHandler.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Toast.makeText(requireContext(), "SQLite Jalan",
                Toast.LENGTH_SHORT).show();

        Users users = dbHandler.getUsers();
        final String idUser = users.getUser_id();

        dbHandler.close();

        Toast.makeText(requireContext(), idUser,
                Toast.LENGTH_SHORT).show();

        final String make_name = make;
        final String model_name = model;
        final String model_type = type;
        final String model_color = color;
        final String model_year = year;
        final String model_mileage = mileage;
        final String model_price = price;
        final String model_desc = desc;
        final String base64Image_1;
        final String base64Image_2;
        final String base64Image_3;

        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();

        bp1.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream1);
        byte[] bytes1 = byteArrayOutputStream1.toByteArray();
        base64Image_1 = Base64.encodeToString(bytes1, Base64.DEFAULT);

        Toast.makeText(requireContext(), "Gambar 1 keload",
                Toast.LENGTH_SHORT).show();

        bp2.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream2);
        byte[] bytes2 = byteArrayOutputStream2.toByteArray();
        base64Image_2 = Base64.encodeToString(bytes2, Base64.DEFAULT);

        Toast.makeText(requireContext(), "Gambar 2 keload",
                Toast.LENGTH_SHORT).show();

        bp1.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream3);
        byte[] bytes3 = byteArrayOutputStream3.toByteArray();
        base64Image_3 = Base64.encodeToString(bytes3, Base64.DEFAULT);

        Toast.makeText(requireContext(), "Gambar 3 keload",
                Toast.LENGTH_SHORT).show();

        //progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constant.URL_ADDCAR,
                response -> {
                    //progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject obj = new JSONObject(response);

                        Toast.makeText(requireContext(), "Mengambil response berhasil",
                                Toast.LENGTH_SHORT).show();

                        if (!obj.getBoolean("error")) {

                            Toast.makeText(requireContext(), obj.getString("message"),
                                    Toast.LENGTH_LONG).show();

                            startActivity(new Intent(requireActivity(), PembayaranActivity.class));
                            ((SellCarActivity)getActivity()).finish();

                        } else {
                            Toast.makeText(requireContext(), obj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    //progressBar.setVisibility(View.GONE);

                    Toast.makeText(requireContext(), "Silahkan cek kembali internet anda!",
                            Toast.LENGTH_SHORT).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", idUser);
                params.put("make_name", make_name);
                params.put("model_name", model_name);
                params.put("model_type", model_type);
                params.put("model_color", model_color);
                params.put("model_year", model_year);
                params.put("model_mileage", model_mileage);
                params.put("model_price", model_price);
                params.put("model_desc", model_desc);
                params.put("carPhoto_1", base64Image_1);
                params.put("carPhoto_2", base64Image_2);
                params.put("carPhoto_3", base64Image_3);
                return params;
            }
        };
        RequestHandler.getInstance(requireContext()).addToRequestQueue(stringRequest);
    }
}