package com.example.jobfinder;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Gói gọn logic tạo NotificationChannel và hiển thị thông báo, để dùng
 * chung ở nhiều nơi (Dashboard, và sau này có thể cả các màn khác) mà
 * không phải lặp lại code.
 */
public class NotificationHelper {

    // ID của channel -- phải giống nhau mỗi lần tạo và mỗi lần gửi thông báo,
    // nếu không Android sẽ coi là 2 channel khác nhau.
    private static final String CHANNEL_ID = "new_applicant_channel";
    private static final String CHANNEL_NAME = "Ứng viên mới";

    /**
     * Tạo channel -- CHỈ CẦN GỌI 1 LẦN trong vòng đời app (gọi nhiều lần
     * cũng không lỗi, Android tự bỏ qua nếu channel đã tồn tại). Thường gọi
     * ngay khi Activity đầu tiên khởi động.
     */
    public static void createNotificationChannel(Context context) {
        // NotificationChannel chỉ tồn tại từ Android 8.0 (API 26) trở lên,
        // nên phải kiểm tra phiên bản Android trước khi gọi, nếu không app
        // sẽ crash trên máy Android cũ hơn.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // mức độ ưu tiên: hiện kèm âm thanh + popup
            );
            channel.setDescription("Thông báo khi có ứng viên mới nộp đơn");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Hiện 1 thông báo. Trả về false nếu không có quyền (để nơi gọi biết
     * và có thể xin quyền), true nếu hiện thành công.
     */
    public static boolean showNotification(Context context, String title, String content, int notificationId) {
        // Từ Android 13 (API 33) trở lên, PHẢI có quyền POST_NOTIFICATIONS
        // mới được hiện thông báo -- đây là quyền "nguy hiểm" (dangerous permission),
        // giống Camera/Location, bắt buộc xin runtime chứ khai trong Manifest thôi chưa đủ.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return false; // chưa có quyền, không hiện được
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: thay bằng icon riêng của app sau
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // tự biến mất khi user bấm vào

        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
        return true;
    }
}