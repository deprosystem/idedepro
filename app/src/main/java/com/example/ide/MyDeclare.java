package com.example.ide;
import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.interfaces_classes.Multiply;
import com.dpcsa.compon.interfaces_classes.ToolMenu;

import static com.dpcsa.compon.interfaces_classes.ToolMenu.STATUS_START.VIEW;
import static com.dpcsa.compon.interfaces_classes.ToolMenu.STATUS_VIEW.ALWAYS;

public class MyDeclare extends DeclareScreens {

    public final static String
            MAIN = "MAIN", NEW = "NEW", SHARE = "SHARE",
            DETAIL = "DETAIL";

    @Override
    public void declare() {
        activity(MAIN, R.layout.activity_main)
                .toolBar(R.id.tool_bar)
                .fragmentsContainer(R.id.container_fragm)
                .menuBottom(model(menuMainMenu_b), view(R.id.menu_b));

        fragment(NEW, R.layout.fragment_new, R.string.new_screen_title)
                .list(model("query/nk3mek0vtopbdoh/4"),
                        view(R.id.list, R.layout.item_new_list_0),
                        navigator(start(DETAIL)));

        fragment(SHARE, R.layout.fragment_share, R.string.share_screen_title);
        activity(DETAIL, R.layout.activity_detail)
                .component(TC.PANEL, model("query/nk3mek0vtopbdoh/7", "id_new"),
                        view(R.id.scroll_panel));

    }

    Menu menuMainMenu_b = new Menu()
            .item(R.drawable.news, R.string.main_menu_b_0, NEW, true)
            .item(R.drawable.promotion, R.string.main_menu_b_1, SHARE);
}
