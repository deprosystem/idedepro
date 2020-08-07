package com.example.ide;

import com.dpcsa.compon.base.DeclareScreens;

public class MyDeclare extends DeclareScreens {

    public final static String
        MAIN = "MAIN";

    @Override
    public void declare() {

        activity(MAIN, R.layout.activity_main)
            .list(model(JSON, "[{\"qwert\":\"https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072823__480.jpg\",\"asdfg\":\"Во какая картинка\",\"zxcvb\":\"Вот это да!!!\"},{\"qwert\":\"https://cdn.pixabay.com/photo/2018/02/08/22/27/flower-3140492__480.jpg\",\"asdfg\":\"Тоже не плохо\",\"zxcvb\":\"Нормально\"},{\"qwert\":\"https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072823__480.jpg\",\"asdfg\":\"Во какая картинка\",\"zxcvb\":\"Вот это да!!!\"},{\"qwert\":\"https://cdn.pixabay.com/photo/2018/02/08/22/27/flower-3140492__480.jpg\",\"asdfg\":\"Тоже не плохо\",\"zxcvb\":\"Нормально\"},{\"qwert\":\"https://i.pinimg.com/474x/95/57/f7/9557f764bcf6a24b8a2e9d9687782cf4.jpg\",\"asdfg\":\"Люблю вкраїнськую природу\",\"zxcvb\":\"Дуже\"},{\"qwert\":\"https://i.pinimg.com/474x/8b/3f/3c/8b3f3cb2f6ddecdf1d0c7e4393039ad9.jpg\",\"asdfg\":\"Гори теж нічого\",\"zxcvb\":\"Може Карпати\"},{\"qwert\":\"https://i.pinimg.com/474x/68/c2/9e/68c29ed7f75c26b786a8427af95ae890.jpg\",\"asdfg\":\"Не погано\",\"zxcvb\":\"Але холодно\"}]"),
                view(R.id.list, R.layout.item_main_list_0).spanCount(2));

    }

}
