package com.example.icetea.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.icetea.scanner.QRScannerFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.example.icetea.R;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllEventsFragment extends Fragment {
    private List<Event> eventList;
    private List<Event> filteredEventList;
    private EventAdapter adapter;
    private TextInputEditText searchEditText;
    private ImageButton filterButton;
    private Date selectedStartDate = null;
    private Date selectedEndDate = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public AllEventsFragment() {
        // Required empty public constructor
    }

    public static AllEventsFragment newInstance() {
        return new AllEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        searchEditText = view.findViewById(R.id.editTextSearch);
        filterButton = view.findViewById(R.id.buttonFilter);

        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAllEvents);
        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();

        adapter = new EventAdapter(filteredEventList, event -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, EventDetailsFragment.newInstance(event.getEventId()));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setupSearch();
        filterButton.setOnClickListener(v -> {
            showFilterDialog();
        });

        loadEvents();
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterEvents(String query) {
        filteredEventList.clear();

        List<Event> eventsToFilter = eventList;


        if (selectedStartDate != null && selectedEndDate != null) {
            List<Event> dateFilteredEvents = new ArrayList<>();

            Calendar startCal = Calendar.getInstance();
            startCal.setTime(selectedStartDate);
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(selectedEndDate);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);

            for (Event event : eventList) {
                if (event.getEventStartDate() != null) {
                    Date eventDate = event.getEventStartDate().toDate();
                    if (eventDate.compareTo(startCal.getTime()) >= 0 &&
                            eventDate.compareTo(endCal.getTime()) <= 0) {
                        dateFilteredEvents.add(event);
                    }
                }
            }
            eventsToFilter = dateFilteredEvents;
        }

        if (query.isEmpty()) {
            filteredEventList.addAll(eventsToFilter);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();

            for (Event event : eventsToFilter) {
                if (event.getName() != null &&
                        event.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredEventList.add(event);
                    continue;
                }
                if (event.getDescription() != null &&
                        event.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredEventList.add(event);
                    continue;
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadEvents() {
        EventDB.getInstance().getActiveEvents(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> fetchedEvents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) {
                        fetchedEvents.add(event);
                    }
                }
                eventList.clear();
                eventList.addAll(fetchedEvents);

                // Apply current search filter
                String currentQuery = searchEditText.getText() != null ?
                        searchEditText.getText().toString() : "";
                filterEvents(currentQuery);
            } else {
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                Log.e("AllEventsFragment", "Failed to fetch events", task.getException());
            }
        });
    }

    private void showFilterDialog() {
        String[] options = {"Filter by Date Range", "Clear Filter"};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filter Events")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showDateRangePicker();
                    } else {
                        clearDateFilter();
                    }
                })
                .show();
    }

    private void showDateRangePicker() {
        Calendar calendar = Calendar.getInstance();


        DatePickerDialog startDatePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar startCal = Calendar.getInstance();
                    startCal.set(year, month, dayOfMonth);
                    selectedStartDate = startCal.getTime();


                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(year, month, dayOfMonth);

                    DatePickerDialog endDatePicker = new DatePickerDialog(
                            requireContext(),
                            (v, y, m, d) -> {
                                Calendar endCal = Calendar.getInstance();
                                endCal.set(y, m, d);
                                selectedEndDate = endCal.getTime();

                                // Ensure start date is before end date
                                if (selectedStartDate.after(selectedEndDate)) {
                                    Toast.makeText(getContext(),
                                            "Start date must be before end date",
                                            Toast.LENGTH_SHORT).show();
                                    selectedStartDate = null;
                                    selectedEndDate = null;
                                    return;
                                }

                                String currentQuery = searchEditText.getText() != null ?
                                        searchEditText.getText().toString() : "";
                                filterEvents(currentQuery);

                                Toast.makeText(getContext(),
                                        "Showing events from " + dateFormat.format(selectedStartDate) +
                                                " to " + dateFormat.format(selectedEndDate),
                                        Toast.LENGTH_SHORT).show();
                            },
                            endCalendar.get(Calendar.YEAR),
                            endCalendar.get(Calendar.MONTH),
                            endCalendar.get(Calendar.DAY_OF_MONTH)
                    );
                    endDatePicker.setTitle("Select End Date");
                    endDatePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        startDatePicker.setTitle("Select Start Date");
        startDatePicker.show();
    }

    private void clearDateFilter() {
        selectedStartDate = null;
        selectedEndDate = null;
        String currentQuery = searchEditText.getText() != null ?
                searchEditText.getText().toString() : "";
        filterEvents(currentQuery);
        Toast.makeText(getContext(), "Date filter cleared", Toast.LENGTH_SHORT).show();
    }
}