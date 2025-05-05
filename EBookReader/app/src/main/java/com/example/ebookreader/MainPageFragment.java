package com.example.ebookreader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // İlgili layout dosyasını burada inflate edeceğiz (bir sonraki adımda oluşturulacak)
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        // Bu view üzerindeki component'lerle ilgili işlemleri onViewCreated'da yapmak daha iyidir.
        return view;
    }

    // Gerekirse onViewCreated metodu da eklenebilir
    // @Override
    // public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    //     super.onViewCreated(view, savedInstanceState);
    //     // UI elemanlarına burada erişmek daha güvenlidir (findViewById vb.)
    // }
}