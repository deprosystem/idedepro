package com.example.ide;

import com.dpcsa.compon.base.DeclareScreens;

public class MyDeclare extends DeclareScreens {

    public final static String
        MAIN = "MAIN";

    @Override
    public void declare() {

        activity(MAIN, R.layout.activity_main)
            .component(TC.RECYCLER, model(JSON, "[{\"qwert\":\"https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072823__480.jpg\",\"asdfg\":\"Во какая картинка\",\"zxcvb\":\"Вот это да!!!\"},{\"qwert\":\"https://cdn.pixabay.com/photo/2018/02/08/22/27/flower-3140492__480.jpg\",\"asdfg\":\"Тоже не плохо\",\"zxcvb\":\"Нормально\"}]"),
                view(R.id.list, R.layout.item_main_list_0));

    }

}
