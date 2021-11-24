package com.ebookfrenzy.gazdinstvaa.ui.findCompany;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.gazdinstvaa.R;
import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;
import com.ebookfrenzy.gazdinstvaa.ui.addCompany.AddCompanyFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FindCompanyRecyclerAdapter extends RecyclerView.Adapter<FindCompanyRecyclerAdapter.ViewHolder> implements Filterable {
    private Context context;
    private Activity activity;
    private List<Company> companyList;
    private final List<Company> originalList;
    private int SEARCH_LENGTH = 0;

    public FindCompanyRecyclerAdapter(Activity activity,Context context, List<Company> companyList) {
        this.context = context;
        this.companyList = companyList;
        this.originalList = companyList;
        this.activity = activity;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.company_recycler_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Company company = companyList.get(position);

        holder.tvName.setText(company.getCompanyName());
        holder.tvAddress.setText(String.format("%s,%s",company.getAddress(),company.getPlace()));
        holder.tvPhone.setText(String.format("%s: %s",company.getContactPerson(),company.getPhoneNumber()));

        holder.parentLayout.setOnClickListener(v -> {
            company.setExpanded(!company.isExpanded());
            if(company.isExpanded()){
                holder.expandableLayout.setVisibility(View.VISIBLE);
            }else{
                holder.expandableLayout.setVisibility(View.GONE);
            }
        });
        holder.iwMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context,holder.iwMenu);
            popupMenu.getMenuInflater().inflate(R.menu.company_menu,popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.company_call){
                        //make call
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + company.getPhoneNumber()));
                        context.startActivity(callIntent);
                        return true;
                    }
                    if(item.getItemId() == R.id.company_address){
                        //directions in google maps
                        Uri gmmIntentUri = Uri.parse("geo:" +
                                company.getLat() +
                                company.getLongatude() +
                                "?q=" + company.getAddress() +
                                " " +
                                company.getPlace());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        context.startActivity(mapIntent);
                        return true;
                    }
                    if(item.getItemId() == R.id.company_edit){
                        //edit company
                        ((AppCompatActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.nav_host_fragment, AddCompanyFragment.newInstance(company))
                                .addToBackStack("EditCompany")
                                .commit();
                        return true;
                    };
                    if(item.getItemId() == R.id.company_delete){
                        //delete company
                        CompanyRepository companyRepository = new CompanyRepository(activity.getApplication());
                        companyRepository.deleteCompany(company);
                        int iPos = 0;
                        for(int i = 0; i < companyList.size(); i++){
                            if(companyList.get(i).getId() == company.getId()){
                                iPos = i;
                            }
                        }
                        companyList.remove(iPos);
                        notifyItemRemoved(iPos);
                        return true;
                    }
                    return false;
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<Company> companies;
                if(constraint.length() == 0){
                    companies = originalList;
                    if(SEARCH_LENGTH > constraint.length()){
                        companies = getFilteredResults(constraint.toString());
                    }
                }else{
                    companies = getFilteredResults(constraint.toString());
                }
                filterResults.values = companies;
                filterResults.count = companies.size();
                SEARCH_LENGTH = constraint.length();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                companyList = (List<Company>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private List<Company> getFilteredResults(String toString) {
        List<Company> results = new ArrayList<>();
        for(Company company:originalList){
            if (company.getCompanyName().toLowerCase().contains(toString.toLowerCase())) {
                results.add(company);
            }
        }
        return results;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvPhone,tvAddress;
        ImageView iwMenu;
        LinearLayout expandableLayout,parentLayout;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.company_recycler_name);
            tvPhone = itemView.findViewById(R.id.company_find_phone);
            tvAddress = itemView.findViewById(R.id.company_find_address);
            iwMenu = itemView.findViewById(R.id.company_find_menu);
            expandableLayout = itemView.findViewById(R.id.find_company_expandable);
            parentLayout = itemView.findViewById(R.id.company_find_parent);
        }
    }
}
