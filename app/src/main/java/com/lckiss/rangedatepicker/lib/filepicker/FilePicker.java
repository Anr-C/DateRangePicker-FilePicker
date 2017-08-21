package com.lckiss.rangedatepicker.lib.filepicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.lckiss.rangedatepicker.R;
import com.lckiss.rangedatepicker.lib.common.popup.ConfirmPopup;
import com.lckiss.rangedatepicker.lib.common.utils.ConvertUtils;
import com.lckiss.rangedatepicker.lib.common.utils.LogUtils;
import com.lckiss.rangedatepicker.lib.filepicker.adapter.FileAdapter;
import com.lckiss.rangedatepicker.lib.filepicker.adapter.PathAdapter;
import com.lckiss.rangedatepicker.lib.filepicker.entity.FileItem;
import com.lckiss.rangedatepicker.lib.filepicker.util.StorageUtils;
import com.lckiss.rangedatepicker.lib.filepicker.widget.HorizontalListView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 文件目录选择器
 */
public class FilePicker extends ConfirmPopup<LinearLayout> implements AdapterView.OnItemClickListener {
    public static final int DIRECTORY = 0;
    public static final int FILE = 1;

    private String initPath;
    private FileAdapter adapter;
    private PathAdapter pathAdapter = new PathAdapter();
    private TextView emptyView;
    private OnFilePickListener onFilePickListener;
    private int mode;
    private CharSequence emptyHint = "<空>";

    @IntDef(value = {DIRECTORY, FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    /**
     * @see #FILE
     * @see #DIRECTORY
     */
    public FilePicker(Activity activity, @Mode int mode) {
        super(activity);
        setHalfScreen(true);
        try {
            this.initPath = StorageUtils.getDownloadPath();
        } catch (RuntimeException e) {
            this.initPath = StorageUtils.getInternalRootPath(activity);
        }
        this.mode = mode;
        adapter = new FileAdapter(activity);
        adapter.setOnlyListDir(mode == DIRECTORY);
        adapter.setShowHideDir(false);
        adapter.setShowHomeDir(false);
        adapter.setShowUpDir(false);
    }

    @Override
    @NonNull
    protected LinearLayout makeCenterView() {
        LinearLayout rootLayout = new LinearLayout(activity);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.WHITE);

        ListView listView = new ListView(activity);
        listView.setBackgroundColor(Color.WHITE);
        listView.setDivider(new ColorDrawable(0xFFDDDDDD));
        listView.setDividerHeight(1);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        rootLayout.addView(listView);

        emptyView = new TextView(activity);
        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        txtParams.gravity = Gravity.CENTER;
        emptyView.setLayoutParams(txtParams);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        emptyView.setTextColor(Color.BLACK);
        rootLayout.addView(emptyView);

        return rootLayout;
    }

    @Nullable
    @Override
    protected View makeFooterView() {
        LinearLayout rootLayout = new LinearLayout(activity);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.WHITE);

        View lineView = new View(activity);
        lineView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 1));
        lineView.setBackgroundColor(0xFFDDDDDD);
        rootLayout.addView(lineView);

        HorizontalListView pathView = new HorizontalListView(activity);
        int height = ConvertUtils.toPx(activity, 30);
        pathView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, height));
        pathView.setAdapter(pathAdapter);
        pathView.setBackgroundColor(Color.WHITE);
        pathView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                refreshCurrentDirPath(pathAdapter.getItem(position));
            }
        });
        rootLayout.addView(pathView);

        return rootLayout;
    }

    public void setRootPath(String initPath) {
        this.initPath = initPath;
    }

    public void setAllowExtensions(String[] allowExtensions) {
        adapter.setAllowExtensions(allowExtensions);
    }

    public void setShowUpDir(boolean showUpDir) {
        adapter.setShowUpDir(showUpDir);
    }

    public void setShowHomeDir(boolean showHomeDir) {
        adapter.setShowHomeDir(showHomeDir);
    }

    public void setShowHideDir(boolean showHideDir) {
        adapter.setShowHideDir(showHideDir);
    }

    public void setEmptyHint(CharSequence emptyHint) {
        this.emptyHint = emptyHint;
    }

    @Override
    protected void setContentViewBefore() {
//        boolean isPickFile = mode == FILE;
//        setCancelVisible(!isPickFile);
//        if (isPickFile) {
//            setSubmitText(activity.getString(android.R.string.cancel));
//        } else {
//            setSubmitText(activity.getString(android.R.string.ok));
//        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 20, 0, 0);
        TextView title = new TextView(activity);
        title.setText("选择文件");
        title.setTextSize(20);
        title.setTextColor(getTxtColor(activity, R.color.message));
        title.setLayoutParams(lp);
        setTopLineColor(getTxtColor(activity,android.R.color.transparent));
        setHeaderView(title);
    }
    /**
     * @param context 上下文
     * @param i       colorid
     * @return 颜色值
     */
    public static int getTxtColor(Context context, int i) {
        return ContextCompat.getColor(context, i);
    }

    @Override
    protected void setContentViewAfter(View contentView) {
        refreshCurrentDirPath(initPath);
    }

    @Override
    protected void onSubmit() {
        if (mode == FILE) {
            LogUtils.verbose("pick file canceled");
        } else {
            String currentPath = adapter.getCurrentPath();
            LogUtils.debug("picked directory: " + currentPath);
            if (onFilePickListener != null) {
                onFilePickListener.onFilePicked(currentPath);
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        adapter.recycleData();
//        pathAdapter.recycleData();
    }

    public FileAdapter getAdapter() {
        return adapter;
    }

    public String getCurrentPath() {
        return adapter.getCurrentPath();
    }

    /**
     * 响应选择器的列表项点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        FileItem fileItem = adapter.getItem(position);
        if (fileItem.isDirectory()) {
            refreshCurrentDirPath(fileItem.getPath());
        } else {
            String clickPath = fileItem.getPath();
            if (mode == DIRECTORY) {
                LogUtils.warn("not directory: " + clickPath);
            } else {
                dismiss();
                LogUtils.debug("picked path: " + clickPath);
                if (onFilePickListener != null) {
                    onFilePickListener.onFilePicked(clickPath);
                }
            }
        }
    }

    private void refreshCurrentDirPath(String currentPath) {
        if (currentPath.equals("/")) {
            pathAdapter.updatePath("/");
        } else {
            pathAdapter.updatePath(currentPath);
        }
        adapter.loadData(currentPath);
        int adapterCount = adapter.getCount();
        if (adapter.isShowHomeDir()) {
            adapterCount--;
        }
        if (adapter.isShowUpDir()) {
            adapterCount--;
        }
        if (adapterCount < 1) {
            LogUtils.verbose(this, "no files, or dir is empty");
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(emptyHint);
        } else {
            LogUtils.verbose(this, "files or dirs count: " + adapterCount);
            emptyView.setVisibility(View.GONE);
        }
    }

    public void setOnFilePickListener(OnFilePickListener listener) {
        this.onFilePickListener = listener;
    }

    public interface OnFilePickListener {

        void onFilePicked(String currentPath);

    }

}
