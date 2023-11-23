package com.example.talentahub

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.talentahub.models.Event
import java.io.File
import java.io.FileOutputStream

class DetailsEvent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_event)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set custom centered title with black text color
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title
        val customTitle = findViewById<TextView>(R.id.custom_title)
        customTitle.text = "Details"
        customTitle.setTextColor(getColor(android.R.color.black)) // Set text color

        val eventImage = findViewById<ImageView>(R.id.eventImage)
        val eventTitle = findViewById<TextView>(R.id.eventTitle)
        val eventDescription = findViewById<TextView>(R.id.eventDescription)
        val eventLocation = findViewById<TextView>(R.id.eventLocation)
        val bookNowButton = findViewById<Button>(R.id.bookNowButton)
        bookNowButton.setOnClickListener {
            generatePDF()
        }
        // Get the event data from the intent
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val image = intent.getStringExtra("image")
        // Update UI with event data
        Glide.with(this).load(image).into(eventImage)
        eventTitle.text = name
        eventDescription.text = description
        eventLocation.text = location
    }
    // Handle back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    // on below line we are creating a generate PDF method
    // which is use to generate our PDF file.
    @RequiresApi(19)
    fun generatePDF() {
        // declaring width and height
        // for our PDF file.
        var pageHeight = 1120
        var pageWidth = 792

        // creating a bitmap variable
        // for storing our images
        lateinit var bmp: Bitmap
        lateinit var scaledbmp: Bitmap

        // on below line we are creating a
        // constant code for runtime permissions.
        var PERMISSION_CODE = 101
        // on below line we are initializing our bitmap and scaled bitmap.
        bmp = BitmapFactory.decodeResource(resources, R.drawable.talentahublogo)
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)

        // on below line we are checking permission
        if (checkPermissions()) {
            // if permission is granted we are displaying a toast message.
            Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show()
        } else {
            // if the permission is not granted
            // we are calling request permission method.
            requestPermission()
        }

        // creating an object variable
        // for our PDF document.
        var pdfDocument: PdfDocument = PdfDocument()

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        var paint: Paint = Paint()
        var title: Paint = Paint()

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        var myPageInfo: PdfDocument.PageInfo? =
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

        // below line is used for setting
        // start page for our PDF file.
        var myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)

        // creating a variable for canvas
        // from our page of PDF.
        var canvas: Canvas = myPage.canvas

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp, 56F, 40F, paint)

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.textSize = 15F

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.black))

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("TalonHub", 209F, 100F, title)
        canvas.drawText("Registration Successful Thank You", 209F, 80F, title)
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.setColor(ContextCompat.getColor(this, R.color.black))
        title.textSize = 15F

        // below line is used for setting
        // our text to center of PDF.
        title.textAlign = Paint.Align.CENTER
        canvas.drawText("Registration Successful Thank You ", 396F, 560F, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage)

        // Set the directory path where you want to save the PDF file in your app's private directory.
        val dir = File(getExternalFilesDir(null), "PDFs")

        // Create the directory if it doesn't exist.
        if (!dir.exists()) {
            dir.mkdirs()
        }
        // Set the name of the PDF file.
        val fileName = "TalonHub.pdf"

        // Create the file in the specified directory.
        val file = File(dir, fileName)
        try {
            // Write the PDF file to the app's private directory.
            pdfDocument.writeTo(FileOutputStream(file))

            // Notify the system to scan the file.
            MediaScannerConnection.scanFile(
                applicationContext,
                arrayOf(file.absolutePath),
                arrayOf("application/pdf")
            ) { _, _ ->
                // Open the generated PDF file using an Intent.
                val pdfIntent = Intent(Intent.ACTION_VIEW)

                // Check if the Android version is 7.0 or above.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // Use FileProvider for Android 7.0 and above
                    val contentUri = FileProvider.getUriForFile(
                        applicationContext,
                        "com.example.talentahub.fileprovider",
                        file
                    )
                    pdfIntent.setDataAndType(contentUri, "application/pdf")
                    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf")
                }

                val chooserIntent = Intent.createChooser(pdfIntent, "Open PDF with")

                // Verify that the intent will resolve to an activity.
                if (pdfIntent.resolveActivity(packageManager) != null) {
                    startActivity(chooserIntent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "No PDF viewer app found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            // below line is used
            // to handle error
            e.printStackTrace()

            // on below line we are displaying a toast message as fail to generate PDF
            Toast.makeText(applicationContext, "Fail to generate PDF file..", Toast.LENGTH_SHORT)
                .show()
        } finally {
            // after storing our pdf to that
            // location we are closing our PDF file.
            pdfDocument.close()
        }
    }

    fun checkPermissions(): Boolean {
        // on below line we are creating a variable for both of our permissions.

        // on below line we are creating a variable for
        // writing to external storage permission
        var writeStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // on below line we are creating a variable
        // for reading external storage permission
        var readStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // on below line we are returning true if both the
        // permissions are granted and returning false
        // if permissions are not granted.
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    // on below line we are creating a function to request permission.
    fun requestPermission() {

        // on below line we are requesting read and write to
        // storage permission for our application.
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 101
        )
    }

    // on below line we are calling
    // on request permission result.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // on below line we are checking if the
        // request code is equal to permission code.
        if (requestCode == 101) {

            // on below line we are checking if result size is > 0
            if (grantResults.size > 0) {

                // on below line we are checking
                // if both the permissions are granted.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {

                    // if permissions are granted we are displaying a toast message.
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()

                } else {

                    // if permissions are not granted we are
                    // displaying a toast message as permission denied.
                    Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
