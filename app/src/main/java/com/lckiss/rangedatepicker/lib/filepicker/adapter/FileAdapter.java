package com.lckiss.rangedatepicker.lib.filepicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lckiss.rangedatepicker.R;
import com.lckiss.rangedatepicker.lib.common.utils.CompatUtils;
import com.lckiss.rangedatepicker.lib.common.utils.LogUtils;
import com.lckiss.rangedatepicker.lib.filepicker.drawable.StateColorDrawable;
import com.lckiss.rangedatepicker.lib.filepicker.entity.FileItem;
import com.lckiss.rangedatepicker.lib.filepicker.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件目录数据适配
 */
public class FileAdapter extends BaseAdapter {
    public static final String DIR_ROOT = ".";
    public static final String DIR_PARENT = "..";
    private ArrayList<FileItem> data = new ArrayList<FileItem>();
    private String rootPath = null;
    private String currentPath = null;
    private String[] allowExtensions = null;//允许的扩展名
    private boolean onlyListDir = false;//是否仅仅读取目录
    private boolean showHomeDir = false;//是否显示返回主目录
    private boolean showUpDir = true;//是否显示返回上一级
    private boolean showHideDir = true;//是否显示隐藏的目录（以“.”开头）
    private Context context;
    private int homeIcon = R.drawable.ic_folder_sd;
    private int upIcon = R.drawable.ic_action_back;
    private int folderIcon = R.drawable.ic_folder;
    private int fileIcon = R.mipmap.ic_launcher;

    /**
     * Instantiates a new File adapter.
     *
     * @param context the context
     */
    public FileAdapter(Context context) {
        this.context = context;
    }


    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * 允许的扩展名
     */
    public void setAllowExtensions(String[] allowExtensions) {
        this.allowExtensions = allowExtensions;
    }

    /**
     * 是否仅仅读取目录
     */
    public void setOnlyListDir(boolean onlyListDir) {
        this.onlyListDir = onlyListDir;
    }

    public boolean isOnlyListDir() {
        return onlyListDir;
    }

    /**
     * 是否显示返回主目录
     */
    public void setShowHomeDir(boolean showHomeDir) {
        this.showHomeDir = showHomeDir;
    }

    public boolean isShowHomeDir() {
        return showHomeDir;
    }

    /**
     * 是否显示返回上一级
     */
    public void setShowUpDir(boolean showUpDir) {
        this.showUpDir = showUpDir;
    }

    public boolean isShowUpDir() {
        return showUpDir;
    }

    /**
     * 是否显示隐藏的目录（以“.”开头）
     */
    public void setShowHideDir(boolean showHideDir) {
        this.showHideDir = showHideDir;
    }

    public boolean isShowHideDir() {
        return showHideDir;
    }

    public void loadData(String path) {
        if (path == null) {
            LogUtils.warn("current directory is null");
            return;
        }
        ArrayList<FileItem> datas = new ArrayList<FileItem>();
        if (rootPath == null) {
            rootPath = path;
        }
        LogUtils.verbose("current directory path: " + path);
        currentPath = path;
        if (showHomeDir) {
            //添加“返回主目录”
            FileItem fileRoot = new FileItem();
            fileRoot.setDirectory(true);
            fileRoot.setIcon(homeIcon);
            fileRoot.setName(DIR_ROOT);
            fileRoot.setSize(0);
            fileRoot.setPath(rootPath);
            datas.add(fileRoot);
        }
        if (showUpDir && !path.equals("/")) {
            //添加“返回上一级目录”
            FileItem fileParent = new FileItem();
            fileParent.setDirectory(true);
            fileParent.setIcon(upIcon);
            fileParent.setName(DIR_PARENT);
            fileParent.setSize(0);
            fileParent.setPath(new File(path).getParent());
            datas.add(fileParent);
        }
        File[] files;
        if (allowExtensions == null) {
            if (onlyListDir) {
                files = FileUtils.listDirs(currentPath);
            } else {
                files = FileUtils.listDirsAndFiles(currentPath);
            }
        } else {
            if (onlyListDir) {
                files = FileUtils.listDirs(currentPath, allowExtensions);
            } else {
                files = FileUtils.listDirsAndFiles(currentPath, allowExtensions);
            }
        }
        if (files != null) {
            for (File file : files) {
                if (!showHideDir && file.getName().startsWith(".")) {
                    continue;
                }
                FileItem fileItem = new FileItem();
                boolean isDirectory = file.isDirectory();
                fileItem.setDirectory(isDirectory);
                if (isDirectory) {
                    fileItem.setIcon(folderIcon);
                    fileItem.setSize(0);
                } else {
                    fileItem.setIcon(fileIcon);
                    fileItem.setSize(file.length());
                }
                fileItem.setName(file.getName());
                fileItem.setPath(file.getAbsolutePath());
                datas.add(fileItem);
            }
        }
        data.clear();
        data.addAll(datas);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public FileItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.file_item, null);
            CompatUtils.setBackground(convertView, new StateColorDrawable(Color.WHITE, Color.LTGRAY));
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.file_dialog_item_img);
            holder.textView = (TextView) convertView.findViewById(R.id.file_dialog_item_name);
            holder.textPath = (TextView) convertView.findViewById(R.id.file_dialog_item_path);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileItem item = data.get(position);
        holder.imageView.setImageResource(item.getIcon());
        holder.textView.setText(item.getName());
        holder.textPath.setText(item.getPath());
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView textPath;
    }

}
