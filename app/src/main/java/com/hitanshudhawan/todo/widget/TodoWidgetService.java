package com.hitanshudhawan.todo.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by hitanshu on 2/3/18.
 */

public class TodoWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TodoViewsFactory(getApplicationContext(), intent);
    }
}
