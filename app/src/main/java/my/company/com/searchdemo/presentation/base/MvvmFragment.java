package my.company.com.searchdemo.presentation.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import my.company.com.searchdemo.BR;
import my.company.com.searchdemo.presentation.helpers.AutoClearedValue;

/**
 * @author Yevgen Oliinykov on 3/16/18.
 */

public class MvvmFragment<B extends ViewDataBinding, VM extends ViewModel> extends Fragment {

    @Inject Class<VM> viewModelClass;

    protected AutoClearedValue<B> binding;
    protected VM viewModel;


    @Inject ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass);
        binding.get().setVariable(BR.vm, viewModel);
    }

    protected final View setAndBindContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, @LayoutRes int layoutResID) {
        B dataBinding = DataBindingUtil.inflate(inflater, layoutResID, container, false);
        binding = new AutoClearedValue<>(this, dataBinding);
        return binding.get().getRoot();
    }
}
