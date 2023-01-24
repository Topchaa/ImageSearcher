package com.example.imagesearcher

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagesearcher.model.Hit
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.android.synthetic.main.list_item.view.*
import java.io.File
import java.io.IOError
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class CustomAdapter(val dataList: MutableList<Hit>): RecyclerView.Adapter<Holder>() {


    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
context = parent.context
        return Holder(LayoutInflater.from(context).inflate(R.layout.list_item,parent,false))

    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: Holder, position: Int) {
      val data = dataList[position]

        val headline  = holder.itemView.heading
        val pic = holder.itemView.img
        val user = holder.itemView.author
        val loadBtn = holder.itemView.download

        val tags = data.tags.toString();
        val hashtagebi = tags.replace(", ","#",ignoreCase = true)
        val name  = data.user

        headline.text ="#"+hashtagebi.toString();
        user.text = "Author: "+name

        Glide.with(context).load(data.webformatURL).into(pic)



     loadBtn.setOnClickListener(){

         var notification = NotificationCompat.Builder(context, "1")
             .setSmallIcon(R.drawable.ic_baseline_image_24)
             .setContentTitle("Image Searcher")
             .setContentText("Image successfully downloaded")
             .setLargeIcon(getBitmapFromURL(data.webformatURL))
             .setStyle(NotificationCompat.BigPictureStyle()
                 .bigPicture(getBitmapFromURL(data.webformatURL))
                 .bigLargeIcon(null)

             )


         val permissionlistener: PermissionListener = object : PermissionListener {
             override fun onPermissionGranted() {

                 val filename = "${System.currentTimeMillis()}.jpg"
                 val downloadUrlOfImage = data.webformatURL
                 val direct = File(
                     Environment
                         .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                         .getAbsolutePath().toString() + "/" +"App photos"+ "/"
                 )


                 if (!direct.exists()) {
                     direct.mkdir()

                 }

                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                 val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
                 val request = DownloadManager.Request(downloadUri)
                 request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                     .setAllowedOverRoaming(false)
                     .setTitle(filename)
                     .setMimeType("image/jpeg")
                     .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                     .setDestinationInExternalPublicDir(
                         Environment.DIRECTORY_PICTURES,
                         File.separator + "App photos" + File.separator.toString() + filename
                     )

                 dm.enqueue(request)





                 with(NotificationManagerCompat.from(context)) {

                     notify(1, notification.build())
                 }


             }

             override fun onPermissionDenied(deniedPermissions: List<String>) {
                 Toast.makeText(
                     context,
                     "Permission Denied\n$deniedPermissions",
                     Toast.LENGTH_SHORT
                 ).show()
             }
         }


         TedPermission.create()
             .setPermissionListener(permissionlistener)
             .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
             .setPermissions(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
             .check();





     }

pic.setOnClickListener(){

    val intent = Intent("custom-message")

      intent.putExtra("links",data.webformatURL);
       LocalBroadcastManager.getInstance(context).sendBroadcast(intent);




}

    }
    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }





    }








