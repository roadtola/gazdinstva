package com.ebookfrenzy.gazdinstvaa.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.gazdinstvaa.R;
import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.MapAnimate;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapsRecycler extends RecyclerView.Adapter<MapsRecycler.ViewHolder> {
    private List<Company> companyList;
    private Context context;
    private MapAnimate mapAnimate;


    public MapsRecycler(List<Company> companyList, Context context) {
        this.companyList = companyList;
        this.context = context;
        this.mapAnimate = (MapAnimate)context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.ss, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MapsRecycler.ViewHolder holder, int position) {
        Company company = companyList.get(position);
        holder.tvName.setText(company.getCompanyName());
        holder.parent.setOnClickListener(v -> {
            mapAnimate.findFocus(company);
        });
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private CardView parent;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.suggestion);
            parent = itemView.findViewById(R.id.ss_parent);
        }
    }
}
