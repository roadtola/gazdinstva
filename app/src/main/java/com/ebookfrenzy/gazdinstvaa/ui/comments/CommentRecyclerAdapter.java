package com.ebookfrenzy.gazdinstvaa.ui.comments;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.ebookfrenzy.gazdinstvaa.model.Comment;
import com.ebookfrenzy.gazdinstvaa.model.CommentRepository;
import com.ebookfrenzy.gazdinstvaa.ui.addComment.AddCommentFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> implements Filterable {

    private List<Comment> commentList;
    private final List<Comment> originalList;
    private final Context context;
    private final Application application;
    private int SEARCH_SIZE = 0;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

    public CommentRecyclerAdapter(List<Comment> commentList, Context context, Application application) {
        this.commentList = commentList;
        this.originalList = commentList;
        this.context = context;
        this.application = application;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.comments_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentRecyclerAdapter.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvComanyName.setText(comment.getCompanyName());
        holder.tvDate.setText(simpleDateFormat.format(comment.getDate()));
        holder.tvTime.setText(comment.getTime());
        holder.tvComment.setText(comment.getComment());

        if(comment.isChecked()){
            holder.checkBox.setVisibility(View.GONE);
            holder.tvComanyName.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1F));
            holder.tvDate.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    3F));
            holder.tvTime.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    3F));
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                comment.setChecked(true);
                CommentRepository commentRepository = new CommentRepository(application);
                commentRepository.editComment(comment);
                notifyItemChanged(position);
            }
        });

        holder.parentLayout.setOnClickListener(v -> {
            comment.setExpanded(!comment.isExpanded());
            if(comment.isExpanded()){
                holder.extendableLayout.setVisibility(View.VISIBLE);
            }else{
                holder.extendableLayout.setVisibility(View.GONE);
            }
        });

        holder.iwMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context,holder.iwMenu);
            popupMenu.getMenuInflater().inflate(R.menu.commet_menu,popupMenu.getMenu());
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.comment_edit){
                    //edit
                    ((AppCompatActivity)context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, AddCommentFragment.newInstance(comment))
                            .addToBackStack("EditComment")
                            .commit();
                }else if(item.getItemId() == R.id.comment_delete){
                    //delete
                    CommentRepository commentRepository = new CommentRepository(application);
                    commentRepository.delete(comment);
                    commentList.remove(position);
                    notifyItemRemoved(position);
                }else{
                    //send email
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //recipients
                    //intent.putExtra(Intent.EXTRA_EMAIL,)
                    String subject = String.format("%s %s",comment.getCompanyName(),simpleDateFormat.format(comment.getDate()));
                    intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                    intent.putExtra(Intent.EXTRA_TEXT,comment.getComment());
                    intent.setType("message/rfc822");
                    //intent.setData(Uri.parse("mailto:"));
                    context.startActivity(intent);
                }
                return true;
            });
        });



    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<Comment> resultList;

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
                commentList = (List<Comment>) results.values;
                notifyDataSetChanged();
            }
        };

    }
    private List<Comment> getFilteredResults(String search) {
        List<Comment> results = new ArrayList<>();
        for(Comment comment : originalList){
            if(comment.getCompanyName().toLowerCase().contains(search.toLowerCase())){
                results.add(comment);
            }
        }
        return results;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvComanyName;
        private final TextView tvTime;
        private final TextView tvDate;
        private final TextView tvComment;
        private final ImageView iwMenu;
        private final LinearLayout extendableLayout;
        private final LinearLayout parentLayout;
        private final CheckBox checkBox;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvComanyName = itemView.findViewById(R.id.comment_company_name);
            tvTime = itemView.findViewById(R.id.comment_row_time);
            tvDate = itemView.findViewById(R.id.comments_row_date);
            tvComment = itemView.findViewById(R.id.comment_row_comment);
            iwMenu = itemView.findViewById(R.id.comment_find_menu);
            extendableLayout = itemView.findViewById(R.id.comment_row_expandable);
            parentLayout = itemView.findViewById(R.id.comments_row_parent);
            checkBox = itemView.findViewById(R.id.comment_check);
        }
    }
}

//try {
//            DateFormat dateFormat = new SimpleDateFormat("hh:mm");
//            Date commentTime = dateFormat.parse(comment.getTime());
//            Date currentTime = Calendar.getInstance().getTime();
//            if(currentTime.after(commentTime) || comment.isChecked()){
//
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
