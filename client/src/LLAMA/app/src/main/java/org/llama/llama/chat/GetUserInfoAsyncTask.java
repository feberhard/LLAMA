//package org.llama.llama.chat;
//
//import android.os.AsyncTask;
//import android.widget.TextView;
//
//import org.jdeferred.DoneCallback;
//import org.jdeferred.Promise;
//import org.llama.llama.model.User;
//import org.llama.llama.services.IUserService;
//
///**
// * Created by Felix on 19.12.2016.
// */
//
//public class GetUserInfoAsyncTask extends AsyncTask<Void, Void, Promise> {
//    private String userId;
//    private TextView userTextView;
//    private IUserService userService;
//
//
//    //http://stackoverflow.com/questions/38598202/recyclerview-and-async-loading
//    public GetUserInfoAsyncTask(String userId, TextView userTextView, IUserService userService) {
//        this.userId = userId;
//        this.userTextView = userTextView;
//        this.userService = userService;
//    }
//
//    //    @Override
////    protected void onPostExecute(){
////
////    }
//    @Override
//    protected void onPostExecute(Promise p) {
//        if(p != null && p.isResolved()){
//            p.done(new DoneCallback() {
//                @Override
//                public void onDone(Object result) {
//                    User user = (User)result;
//                    userTextView.setText(user.getName());
//                }
//            });
//        }
//    }
//
//    @Override
//    protected Promise doInBackground(Void... params) {
//        Promise p = userService.getUserInfo(userId);
//        try {
//            p.waitSafely();
//            return p;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
//
//
//
