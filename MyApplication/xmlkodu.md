<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/solution_tv"
        android:layout_width="380dp"
        android:layout_height="52dp"
        android:layout_above="@id/result_tv"
        android:layout_marginStart="16dp"
        android:layout_marginLeft="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginRight="16dp"
        android:layout_marginBottom="18dp"
        android:text="0"
        android:textAlignment="textEnd"
        android:textColor="@color/black"
        android:textSize="32sp" />


    <TextView
        android:id="@+id/result_tv"
        android:layout_width="380dp"
        android:layout_height="52dp"
        android:layout_above="@id/buttons_layout"
        android:layout_marginStart="16dp"
        android:layout_marginLeft="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginRight="16dp"
        android:layout_marginBottom="16dp"
        android:text="0"
        android:textAlignment="textEnd"
        android:textColor="@color/black"
        android:textSize="45sp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:layout_alignParentBottom="true"
        android:id="@+id/buttons_layout">


        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal">

            <com.google.android.material.button.MaterialButton
                android:id="@+id/buttonClear"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:layout_width="72dp"
                android:layout_height="72dp"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:text="C"
                android:textColor="@color/black"
                android:textSize="32sp"
                app:cornerRadius="36dp" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/button_open_bracket"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:layout_width="72dp"
                android:layout_height="72dp"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:text="("
                android:textColor="@color/black"
                android:textSize="32sp"
                app:cornerRadius="36dp" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/button_close_bracket"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:layout_width="72dp"
                android:layout_height="72dp"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:text=")"
                android:textColor="@color/black"
                android:textSize="32sp"
                app:cornerRadius="36dp" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/buttonDivide"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:layout_width="72dp"
                android:layout_height="72dp"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:text="/"
                android:textColor="@color/black"
                android:textSize="32sp"
                app:cornerRadius="36dp" />


        </LinearLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal">

            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button7"
                android:backgroundTint="#cbcbcb"
                android:text="7"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button8"
                android:backgroundTint="#cbcbcb"
                android:text="8"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button9"
                android:backgroundTint="#cbcbcb"
                android:text="9"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:id="@+id/buttonMultiply"
                android:text="*"
                />


        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal">

            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button4"
                android:backgroundTint="#cbcbcb"
                android:text="4"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button5"
                android:backgroundTint="#cbcbcb"
                android:text="5"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button6"
                android:backgroundTint="#cbcbcb"
                android:text="6"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:id="@+id/buttonAdd"
                android:text="+"
                />


        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal">

            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button1"
                android:backgroundTint="#cbcbcb"
                android:text="1"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button2"
                android:backgroundTint="#cbcbcb"
                android:text="2"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button3"
                android:backgroundTint="#cbcbcb"
                android:text="3"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:id="@+id/buttonSubtract"
                android:text="-"
                />


        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal">

            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="22sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button_AC"
                android:backgroundTint="#cbcbcb"
                android:text="AC"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button0"
                android:backgroundTint="#cbcbcb"
                android:text="0"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button_dot"
                android:backgroundTint="#cbcbcb"
                android:text="."
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:id="@+id/buttonEqual"
                android:text="="
                />


        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal">

            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button_factorial"
                android:backgroundTint="#cbcbcb"
                android:text="!"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button_us"
                android:backgroundTint="#cbcbcb"
                android:text="^"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="32sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:id="@+id/button_karekok"
                android:backgroundTint="#cbcbcb"
                android:text="√"
                />
            <com.google.android.material.button.MaterialButton
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:cornerRadius="36dp"
                style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
                android:textSize="12sp"
                android:textColor="@color/black"
                android:layout_margin="12dp"
                android:backgroundTint="#cbcbcb"
                android:id="@+id/button_exit"
                android:text="Quit"
                />


        </LinearLayout>

    </LinearLayout>

</RelativeLayout>

