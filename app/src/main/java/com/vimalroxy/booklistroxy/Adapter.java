package com.vimalroxy.booklistroxy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<Book> {

    public Adapter(Context context, ArrayList<Book> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_view,parent,false);
        }

        Book currentBook = getItem(position);

        TextView bookName = (TextView) listItemView.findViewById(R.id.bookname);
        bookName.setText(currentBook.getBookName());

        TextView bookAuthor = (TextView) listItemView.findViewById(R.id.author);
        bookAuthor.setText(currentBook.getAuthor());

        return listItemView;
    }
}
