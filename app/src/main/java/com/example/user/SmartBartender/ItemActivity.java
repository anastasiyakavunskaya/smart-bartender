package com.example.user.SmartBartender;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


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

    private void init(){
        //получаем информацию - какое из активити запущено (ингредиенты или рецепты)
        final boolean layerRecipes = getIntent().getBooleanExtra("layerRecipes",true);
        if(layerRecipes) setTitle("Слоёные коктейли");
        else setTitle("Простые коктейли");

        //инициализация RecycleView
        RecyclerView recycler = findViewById(R.id.item_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(mLayoutManager);

        //адаптер для RecycleView для работы с элементами
        adapter = new ItemAdapter(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                //вызов фрагмента редактирования
                FragmentManager manager = getSupportFragmentManager();
                if(layerRecipes) {
                    EditLayerRecipesFragment mLFragment = presenter.newLayerInstance(true, item);
                    mLFragment.show(manager, "dialog");
                }
                else {
                    EditSimpleRecipesFragment mSFragment = presenter.newSimpleInstance(true, item);
                    mSFragment.show(manager, "dialog");
                }
            }

            @Override
            public void onButtonClick(String item) {
                presenter.onCookClick(item, layerRecipes);
            }
        });
        recycler.setAdapter(adapter);

        ItemModel itemModel = new ItemModel(this);
        //создание презентера
        presenter = new ItemPresenter(itemModel, layerRecipes);
        presenter.attachView(this);
        if(presenter.listOfRecipesIsEmpty(layerRecipes)) {
            TextView emptyWarning = findViewById(R.id.empty_list);
            emptyWarning.setText("Список рецептов пуст");
        } else presenter.viewIsReady();

        //инициализация кнопки добавления
        FloatingActionButton addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //вызов фрагмента добавления
                FragmentManager manager = getSupportFragmentManager();
                if(layerRecipes) {
                    EditLayerRecipesFragment mLFragment = presenter.newLayerInstance(false, null);
                    mLFragment.show(manager, "dialog");
                }
                else {
                    EditSimpleRecipesFragment mSFragment = presenter.newSimpleInstance(false, null);
                    mSFragment.show(manager, "dialog");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void showItems(ArrayList<Pair<String,Integer>> list){
        adapter.setData(list);
    }

    public void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
}
