/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrfmeshprovisioner.adapter;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.MeshNetwork;
import no.nordicsemi.android.meshprovisioner.transport.MeshModel;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;
import no.nordicsemi.android.nrfmeshprovisioner.R;
import no.nordicsemi.android.nrfmeshprovisioner.viewmodels.MeshNetworkLiveData;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private static final int COLUMN_COUNT = 3;
    private final ArrayList<Group> mGroups = new ArrayList<>();
    private final Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private MeshNetwork mNetwork;

    public GroupAdapter(final FragmentActivity context, final MeshNetworkLiveData meshNetworkLiveData, final LiveData<List<Group>> groupLiveData) {
        this.mContext = context;
        meshNetworkLiveData.observe(context, networkLiveData -> {
            if (networkLiveData != null) {
                mNetwork = networkLiveData.getMeshNetwork();
                /*mGroups.clear();
                mGroups.addAll(mNetwork.getGroups());
                notifyDataSetChanged();*/
            }
        });

        groupLiveData.observe(context, groups -> {
            if(groups != null && !groups.isEmpty()) {
                mGroups.clear();
                mGroups.addAll(groups);
                notifyDataSetChanged();
            }
        });
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View layoutView = LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupAdapter.ViewHolder holder, final int position) {
        if (mGroups.size() > 0) {
            final Group group = mGroups.get(position);
            if (group != null) {
                final List<MeshModel> models = mNetwork.getModels(group);
                holder.groupName.setText(group.getName());
                final String addressSummary = "Address: " + MeshParserUtils.bytesToHex(group.getGroupAddress(), true);
                holder.groupAddress.setText(addressSummary);
                holder.groupDeviceCount.setText(mContext.getString(R.string.group_device_count, models.size()));
            }
        }
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(final byte[] address);
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.group_container)
        ConstraintLayout container;
        @BindView(R.id.group_name)
        TextView groupName;
        @BindView(R.id.group_address)
        TextView groupAddress;
        @BindView(R.id.group_device_count)
        TextView groupDeviceCount;

        private ViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            container.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mGroups.get(getAdapterPosition()).getGroupAddress());
                }
            });
        }
    }
}
