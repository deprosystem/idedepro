package com.example.ide;
import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.interfaces_classes.Multiply;
import com.dpcsa.compon.interfaces_classes.ToolMenu;

import static com.dpcsa.compon.interfaces_classes.ToolMenu.STATUS_START.VIEW;
import static com.dpcsa.compon.interfaces_classes.ToolMenu.STATUS_VIEW.ALWAYS;

public class MyDeclare extends DeclareScreens {

    public final static String
            MAIN = "MAIN", NEWS = "NEWS", EVE = "EVE", ZZZ = "ZZZ",
            BEF = "BEF", AFT = "AFT", DETAIL = "DETAIL", REPAIRS_CALC = "REPAIRS_CALC";

    @Override
    public void declare() {
        activity(MAIN, R.layout.activity_main)
                .componentPhoto(R.id.cli, new int[] {R.id.blur, R.id.photo}, R.string.source_photo)
                .component(TC.PANEL_ENTER, null,
                        view(R.id.form),
                        navigator(handler(R.id.send, VH.CLICK_SEND, model(POST, "query/o_tfsqz6uw0n058/29", "aa,photo,ss,dd")
                                , after(start(ZZZ)),
                                false, R.id.ss)));


//            .navigator(start(R.id.sss, REPAIRS_CALC), start(R.id.aft, AFT))


//                .plusMinus(R.id.count, R.id.plus, R.id.minus, null, new Multiply(0, "price", "sum"))
//                .list(model("query/s3rmbcskuwiaqbf/1"), view(R.id.list, R.layout.item_main_list_0))
//                .componentTotal(R.id.total, R.id.list, R.id.count, null, "sum")
        ;

        activity(ZZZ, R.layout.activity_zzz);

        activity(AFT, R.layout.aft).animate(AS.RL)
//                .plusMinus(R.id.count, R.id.plus, R.id.minus, null, new Multiply(0, "price", "sum"))
//                .list(model("query/s3rmbcskuwiaqbf/1"), view(R.id.list, R.layout.item_main_list_0))
//                .componentTotal(R.id.total, R.id.list, R.id.count, null, "sum")

                .component(TC.PANEL_ENTER, model("query/o_tfsqz6uw0n058/28"),
                        view(R.id.form),
                        navigator(handler(R.id.send, VH.CLICK_SEND, model(POST, "query/o_tfsqz6uw0n058/29", "aa,ss,dd")
                                , after(),
                                false, R.id.ss)))



//                .plusMinus(R.id.total, R.id.plus, R.id.minus, null,
//                        new Multiply(0, "price", "amount"))
//                .list(model("https://deprosystem.com/depro/services/service"),
//                        view(R.id.list, R.layout.item_repairs_calc))
//                .componentTotal(R.id.sum, R.id.list, R.id.total, null, "amount");
        ;

        activity(REPAIRS_CALC, R.layout.fragment_repairs_calc).animate(AS.RL)
                .plusMinus(R.id.total, R.id.plus, R.id.minus, null,
                        new Multiply(0, "price", "amount"))
                .list(model("https://deprosystem.com/depro/services/service"),
                        view(R.id.list, R.layout.item_repairs_calc))
                .componentTotal(R.id.sum, R.id.list, R.id.total, null, "amount");

//        activity(MAIN, R.layout.activity_main_1)
//                .navigator(checked(R.id.sw, after(hide(R.id.offf),
//                        show(R.id.onnn)),
//                    after(hide(R.id.onnn),
//                        show(R.id.offf))));

//        activity(MAIN, R.layout.activity_main)
//                .toolBar(R.id.tool_bar, tm)
//                .fragmentsContainer(R.id.container_fragm)
//                .menuBottom(model(menuMainMenu_b), view(R.id.menu_b))
//        ;
//
//        fragment(NEWS, R.layout.fragment_news, R.string.news_screen_title)
//                .list(model("query/va3wf9lx_fvx8to/1"),
//                        view(R.id.list, R.layout.item_news_list_0),
//                        navigator(start(DETAIL)));
//
//        fragment(EVE, R.layout.fragment_eve, R.string.eve_screen_title)
//                .component(TC.PAGER_F, view(R.id.pager,
//                        new String[] {BEF, AFT})
//                        .setTab(R.id.tab_layout, R.array.eve_tab_layout));
//
//        fragment(BEF, R.layout.fragment_bef)
//                .list(model("query/va3wf9lx_fvx8to/2"),
//                        view(R.id.list, R.layout.item_bef_list_0));
//
//        fragment(AFT, R.layout.fragment_aft)
//                .list(model("query/va3wf9lx_fvx8to/3"),
//                        view(R.id.list, R.layout.item_aft_list_0));
//
//        fragment(DETAIL, R.layout.fragment_detail)
//                .component(TC.PANEL, model("query/va3wf9lx_fvx8to/4", "id_news"),
//                        view(R.id.scroll_panel));

    }

//    Menu menuMainMenu_b = new Menu()
//            .item(R.drawable.news, R.string.main_menu_b_0, NEWS, true)
//            .item(R.drawable.even, R.string.main_menu_b_1, EVE);
//
//    ToolMenu tm = new ToolMenu()
//            .item(R.drawable.icon_menu_settings, null, ALWAYS, VIEW);
}
