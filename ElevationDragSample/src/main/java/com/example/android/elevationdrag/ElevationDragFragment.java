/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.elevationdrag;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Outline;
import android.graphics.Path;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.common.logger.Log;

public class ElevationDragFragment extends Fragment {

    public static final String TAG = "ElevationDragFragment";

    /* How much to translate each time the Z+ and Z- buttons are clicked. */
    private static final int ELEVATION_STEP = 40;

    /* Different outlines: */

    private Outline mOutlineCircle;

    private Outline mOutlineRect;

    /* The current elevation of the floating view. */
    private float mElevation = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ztranslation, container, false);

        /* Create different outlines. */
        createOutlines();

        /* Find the {@link View} to apply z-translation to. */
        final View floatingShape = rootView.findViewById(R.id.circle);

        /* Define the shape of the {@link View}'s shadow by setting one of the {@link Outline}s. */
        floatingShape.setOutline(mOutlineCircle);

        /* Clip the {@link View} with its outline. */
        floatingShape.setClipToOutline(true);

        DragFrameLayout dragLayout = ((DragFrameLayout) rootView.findViewById(R.id.main_layout));

        dragLayout.setDragFrameController(new DragFrameLayout.DragFrameLayoutController() {

            @Override
            public void onDragDrop(boolean captured) {
                /* Animate the translation of the {@link View}. Note that the translation
                 is being modified, not the elevation. */
                floatingShape.animate()
                        .translationZ(captured ? 50 : 0)
                        .setDuration(100);
                Log.d(TAG, captured ? "drag" : "drop");
            }
        });

        dragLayout.addDragView(floatingShape);

        /* Raise the circle in z when the "z+" button is clicked. */
        rootView.findViewById(R.id.raise_bt).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mElevation += ELEVATION_STEP;
                Log.d(TAG, String.format("Elevation: %.1f", mElevation));
                floatingShape.setElevation(mElevation);
            }
        });

        /* Lower the circle in z when the "z-" button is clicked. */
        rootView.findViewById(R.id.lower_bt).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mElevation -= ELEVATION_STEP;
                // Don't allow for negative values of Z.
                if (mElevation < 0) {
                    mElevation = 0;
                }
                Log.d(TAG, String.format("Elevation: %.1f", mElevation));
                floatingShape.setElevation(mElevation);
            }
        });

        /* Create a spinner with options to change the shape of the object. */
        Spinner spinner = (Spinner) rootView.findViewById(R.id.shapes_spinner);
        spinner.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.shapes)));

        /* Set the appropriate outline when an item is selected. */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* Set the corresponding Outline to the shape. */
                switch (position) {
                    case 0:
                        floatingShape.setOutline(mOutlineCircle);
                        floatingShape.setClipToOutline(true);
                        break;
                    case 1:
                        floatingShape.setOutline(mOutlineRect);
                        floatingShape.setClipToOutline(true);
                        break;
                    default:
                        floatingShape.setOutline(mOutlineCircle);
                        /* Don't clip the view to the outline in the last case. */
                        floatingShape.setClipToOutline(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                floatingShape.setOutline(mOutlineCircle);
            }
        });

        return rootView;

    }

    private void createOutlines() {
        /* Outlines define the shape that's used for clipping the View and its shadow.  */

        /* Get the size of the shape from resources. */
        int shapeSize = getResources().getDimensionPixelSize(R.dimen.shape_size);

        /* Create a circular outline. */
        mOutlineCircle = new Outline();
        mOutlineCircle.setRoundRect(0, 0, shapeSize, shapeSize, shapeSize / 2);

        /* Create a rectangular outline. */
        mOutlineRect = new Outline();
        mOutlineRect.setRoundRect(0, 0, shapeSize, shapeSize, shapeSize / 10);
    }
}