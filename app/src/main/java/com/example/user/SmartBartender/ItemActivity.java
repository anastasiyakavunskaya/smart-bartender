package com.example.user.SmartBartender;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity {

    private ItemPresenter presenter;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        init();
    }

    public void init(){
        //получаем информацию - какое из активити запущено (ингредиенты или рецепты)
        final boolean isIngredient = getIntent().getBooleanExtra("isIngredient",true);
        if(isIngredient) setTitle("Ингредиенты");
        else setTitle("Рецепты");
        //инициализация RecycleView
        RecyclerView recycler = findViewById(R.id.item_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(mLayoutManager);
        //адаптер для RecycleView для работы с элементами
        adapter = new ItemAdapter(isIngredient, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                //вызов фрагмента редактирования
                FragmentManager manager = getSupportFragmentManager();
                EditFragment mFragment = presenter.newInstance(isIngredient,"Изменить", item);
                mFragment.show(manager, "dialog");
            }

            @Override
            public void onButtonClick(String item) {
                presenter.onCookClick(item);
            }
        });
        recycler.setAdapter(adapter);
        //создание модели
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        ItemModel itemModel = new ItemModel(dbHelper);
        //создание презентера
        presenter = new ItemPresenter(itemModel, isIngredient);
        presenter.attachView(this);
        presenter.viewIsReady();

        //инициализация кнопки добавления
        FloatingActionButton addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //вызов фрагмента добавления
                FragmentManager manager = getSupportFragmentManager();
                EditFragment mFragment = presenter.newInstance(isIngredient,"Добавить", null);
                mFragment.show(manager, "dialog");
            }
        });
    }

    public void showItems(ArrayList<String> list){
        adapter.setData(list);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }



}
