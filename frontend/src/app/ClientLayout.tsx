"use client";

import { AuthContext, useAuthContext } from "@/global/auth/hooks/useAuth";

import Link from "next/link";
import { useRouter } from "next/navigation";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const authState = useAuthContext();
  const router = useRouter();

  const { loginMember, isLogin, logout: _logout } = authState;

  const logout = () => {
    _logout(() => router.replace("/"));
  };

  return (
    <AuthContext value={authState}>
      <header>
        <nav className="flex">
          <Link href="/" className="p-2 rounded hover:bg-gray-100">
            메인
          </Link>
          <Link href="/posts" className="p-2 rounded hover:bg-gray-100">
            글 목록
          </Link>
          {!isLogin && (
            <Link
              href="/members/login"
              className="p-2 rounded hover:bg-gray-100"
            >
              로그인
            </Link>
          )}
          {isLogin && (
            <button onClick={logout} className="p-2 rounded hover:bg-gray-100">
              로그아웃
            </button>
          )}
          {isLogin && (
            <Link href="/members/me" className="p-2 rounded hover:bg-gray-100">
              {loginMember.name}님의 정보
            </Link>
          )}
        </nav>
      </header>
      <main className="flex-1 flex flex-col">{children}</main>
      <footer className="text-center p-2">푸터</footer>
    </AuthContext>
  );
}
