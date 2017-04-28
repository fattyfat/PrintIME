package fatty.printime;

import android.inputmethodservice.InputMethodService;
import android.view.View;

public class PrintActivity extends InputMethodService {

    fatty.printime.PrintActivity_View PrintActivity_View;

    @Override
    public View onCreateInputView()
    {
        PrintActivity_View = (fatty.printime.PrintActivity_View) getLayoutInflater().inflate(R.layout.input, null);
        PrintActivity_View.setIME(this);

        return PrintActivity_View;
    }
}
