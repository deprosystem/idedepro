package com.example.auto;

import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class MyDeclare extends DeclareScreens {

    public final static String
        MAIN = "MAIN", NEWS = "NEWS", PROM = "PROM", 
        DETAIL = "DETAIL", SET = "SET";


    @Override
    public void declare() {

        activity(MAIN, R.layout.activity_main)
            .component(TC.TOOL, null, view(R.id.tool_bar, new int[] {R.drawable.nar_light, 0, 0}))
            .fragmentsContainer(R.id.container_fragm)
            .menuBottom(model(menuMainMenu_b), view(R.id.menu_b));

        fragment(NEWS, R.layout.fragment_news, R.string.news_screen_title)
            .list(model("query/uyrz0xz0st6tek9/5"),
                view(R.id.list, R.layout.item_news_list_0),
                navigator(start(DETAIL)));

        fragment(PROM, R.layout.fragment_prom, R.string.prom_screen_title)
            .list(model("query/uyrz0xz0st6tek9/4"),
                view(R.id.list, R.layout.item_prom_list_0));

        fragment(DETAIL, R.layout.fragment_detail)
            .component(TC.PANEL, model("query/uyrz0xz0st6tek9/6", "id_news"),
                view(R.id.scroll_panel));

        fragment(SET, R.layout.fragment_set, R.string.set_screen_title)
                .subscribeTopic(R.id.sub_news, getString(R.string.set_subfirebase));

        channel("news_ev", "Новости и акции", IMPORTANCE_HIGH, MainActivity.class,
            notices(
            notice("news")
                .lotPushs("Новости", true)
                .iconLarge(R.drawable.promotion)
                .icon(R.drawable.even, getColor(R.color.color_100)),
            notice("event")
                .lotPushs("Акции", true)
                .iconLarge(R.drawable.promotion)
                .icon(R.drawable.news, getColor(R.color.accentDark))))
            .icon(R.drawable.promotion)
            .iconLarge(R.drawable.promotion)
            .iconColor(R.color.accentDark);
    }

    Menu menuMainMenu_b = new Menu()
        .item(R.drawable.news, R.string.main_menu_b_0, NEWS, true)
        .item(R.drawable.promotion, R.string.main_menu_b_1, PROM)
        .item(R.drawable.icon_menu_settings, R.string.main_menu_b_2, SET);

}
