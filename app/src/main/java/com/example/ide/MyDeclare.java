package com.example.ide;
import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;

public class MyDeclare extends DeclareScreens {
    public final static String
            MAIN = "MAIN", NEWS = "NEWS", EVENTS = "EVENTS",
            DETAIL_N = "DETAIL_N", BEF = "BEF", AFT = "AFT";

    @Override
    public void declare() {

        activity(MAIN, R.layout.activity_main)
                .toolBar(R.id.tool_bar)
                .fragmentsContainer(R.id.container_fragm)
                .menuBottom(model(menuMainMenu_b), view(R.id.menu_b));

        fragment(NEWS, R.layout.fragment_news, R.string.news_screen_title)
                .list(model("query/k_c4p56ugt0yls4/1"),
                        view(R.id.list, R.layout.item_news_list_0),
                        navigator(start(DETAIL_N)));

        fragment(EVENTS, R.layout.fragment_events, R.string.events_screen_title)
                .component(TC.PAGER_F, view(R.id.pager,
                        new String[] {BEF, AFT})
                        .setTab(R.id.tab_layout, R.array.events_tab_layout));

        activity(DETAIL_N, R.layout.activity_detail_n)
                .component(TC.PANEL, model("query/k_c4p56ugt0yls4/2", "id_news"),
                        view(R.id.scroll_panel));

        fragment(BEF, R.layout.fragment_bef, R.string.bef_screen_title)
                .list(model("query/k_c4p56ugt0yls4/3"),
                        view(R.id.list, R.layout.item_bef_list_0));

        fragment(AFT, R.layout.fragment_aft, R.string.aft_screen_title)
                .list(model("query/k_c4p56ugt0yls4/4"),
                        view(R.id.list, R.layout.item_aft_list_0));

    }

    Menu menuMainMenu_b = new Menu()
            .item(R.drawable.news, R.string.main_menu_b_0, NEWS, true)
            .item(R.drawable.even, R.string.main_menu_b_1, EVENTS);
}
