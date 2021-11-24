package com.ebookfrenzy.gazdinstvaa.ui.comments;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> names;
    private List<String> originalList;
    private int SEARCH_SIZE = 0;

    public AutoCompleteAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {

        super(context, resource, textViewResourceId, objects);
        names = objects;
        originalList = new ArrayList<>(names);

    }
    @Override
    public int getCount() {
        if(names != null){
            return names.size();
        }else{
            return 0;
        }

    }

    @Nullable
    @Override
    public String getItem(int position) {
        return names.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<String> resultList;

                if(constraint.length() == 0 ){
                    resultList = originalList;
                    if(SEARCH_SIZE > constraint.length()){
                        resultList = getFilteredResults(constraint.toString());
                    }

                }else{
                    resultList = getFilteredResults(constraint.toString());
                }
                filterResults.values = resultList;
                filterResults.count = resultList.size();
                SEARCH_SIZE = constraint.length();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                names = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private List<String> getFilteredResults(String toString) {
        List<String> results = new ArrayList<>();
        for(String string : originalList){
            if(string.toLowerCase().contains(toString.toLowerCase())){
                results.add(string);
            }
        }
        return results;
    }
}
