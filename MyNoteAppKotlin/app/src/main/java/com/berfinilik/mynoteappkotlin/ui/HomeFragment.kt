package com.berfinilik.mynoteappkotlin.ui

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.berfinilik.mynoteappkotlin.R
import com.berfinilik.mynoteappkotlin.adapter.NotesAdapter
import com.berfinilik.mynoteappkotlin.databinding.FragmentHomeBinding
import com.berfinilik.mynoteappkotlin.db.Notes
import com.berfinilik.mynoteappkotlin.db.NotesDatabase
import com.berfinilik.mynoteappkotlin.mvvm.NotesFactoryViewModel
import com.berfinilik.mynoteappkotlin.mvvm.NotesRepository
import com.berfinilik.mynoteappkotlin.mvvm.NotesViewModel


class HomeFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: NotesAdapter
    lateinit var viewModel: NotesViewModel
    var searchNotes = arrayListOf<Notes>()
    var isopened = false

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)


        (activity as AppCompatActivity).supportActionBar

        binding.fab.setOnClickListener {

            view?.findNavController()?.navigate(R.id.action_homeFragment2_to_createNoteFragment2)
        }


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)

        setHasOptionsMenu(true)



        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        val dao = NotesDatabase.getInstance(requireContext()).notesDao
        val repostiry = NotesRepository(dao)
        val factory = NotesFactoryViewModel(repostiry)
        viewModel = ViewModelProvider(this, factory)[NotesViewModel::class.java]

        var placeHolder = listOf<Notes>()
        adapter = NotesAdapter(placeHolder)
        viewModel.displayAllnotes.observe(viewLifecycleOwner, Observer {

            searchNotes = it as ArrayList<Notes>
            placeHolder = it as List<Notes>
            adapter = NotesAdapter(placeHolder)

            binding.recyclerView.adapter = adapter


        })


        return binding.root
    }




    @Suppress("DEPRECATION")
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        var menuItem = menu.findItem(R.id.app_bar_search)
        val searchView  = menuItem.actionView as SearchView



        searchView.setOnSearchClickListener {

            val menuItem = menu.findItem(R.id.delete)
            menuItem.setVisible(false)
            isopened = true


        }

        searchView.setOnCloseListener(SearchView.OnCloseListener { isopened
            val menuItem = menu.findItem(R.id.delete)
            menuItem.setVisible(true)

            isopened.not()

        })

        searchView.queryHint = "Notlarda Ara"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                notesFilter(p0)

                return true

            }


        })



        super.onCreateOptionsMenu(menu, inflater)



    }

    private fun notesFilter(p0: String?) {

        val newfilteredlist = arrayListOf<Notes>()

        for (i in searchNotes){

            if (i.title.contains(p0!!) ){

                newfilteredlist.add(i)
            }
        }

        adapter.filteredList(newfilteredlist)

    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.delete) {
            showDeleteConfirmationDialog()
        }
        return true
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tüm Notları Sil")
        builder.setMessage("Tüm notları silmek istediğinizden emin misiniz?")
        builder.setPositiveButton("Evet") { _, _ ->
            viewModel.clearAll()
            Toast.makeText(context, "TÜM NOTLAR SİLİNDİ", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Hayır") { _, _ ->
            // Kullanıcı işlemi iptal etti, herhangi bir şey yapmayabilirsiniz.
        }
        val dialog = builder.create()
        dialog.show()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                requireActivity().finish()
                requireActivity().moveTaskToBack(true);

            }
        })
    }



}