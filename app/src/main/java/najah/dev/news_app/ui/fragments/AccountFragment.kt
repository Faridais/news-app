package najah.dev.news_app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import najah.dev.news_app.databinding.FragmentAccountBinding
import najah.dev.news_app.ui.ResetActivity
import najah.dev.news_app.ui.SignInActivity
import najah.dev.news_app.ui.SignUpActivity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logOut.setOnClickListener{
            logOut()
        }

        binding.reset.setOnClickListener {
            reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun logOut(){
        val intent = Intent(activity, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun reset(){
        val intent = Intent(activity, ResetActivity::class.java)
        startActivity(intent)
    }
}